/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.weld;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * This class provides file-system orientated scanning
 * 
 * @author Pete Muir
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ModularFileSystemURLHandler
{
   private static final Logger log = Logger.getLogger(ModularFileSystemURLHandler.class.getName());

   @SuppressWarnings("unused")
   private ResourceLoader resourceLoader;

   public ModularFileSystemURLHandler(ResourceLoader resourceLoader)
   {
      this.resourceLoader = resourceLoader;
   }

   public void handle(Collection<String> paths, List<String> discoveredClasses, List<URL> discoveredBeansXmlUrls)
   {
      for (String urlPath : paths)
      {
         try
         {
            log.log(Level.FINEST, "Path: " + urlPath);

            if (urlPath.startsWith("file:"))
            {
               urlPath = urlPath.substring(5);
            }
            if (urlPath.indexOf('!') > 0)
            {
               urlPath = urlPath.substring(0, urlPath.indexOf('!'));
            }

            File file = new File(urlPath);
            if (file.isDirectory())
            {
               handleDirectory(file, null, discoveredClasses, discoveredBeansXmlUrls);
            }
            else
            {
               handleArchiveByFile(file, discoveredClasses, discoveredBeansXmlUrls);
            }
         }
         catch (IOException ioe)
         {
            log.log(Level.FINE, "Could not read entries", ioe);
         }
      }
   }

   private void handleArchiveByFile(File file, List<String> discoveredClasses, List<URL> discoveredBeansXmlUrls)
            throws IOException
   {
      try
      {
         log.log(Level.FINEST, "Archive: " + file);

         String archiveUrl = "jar:" + file.toURI().toURL().toExternalForm() + "!/";
         ZipFile zip = new ZipFile(file);
         Enumeration<? extends ZipEntry> entries = zip.entries();

         while (entries.hasMoreElements())
         {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            handle(name, new URL(archiveUrl + name), discoveredClasses, discoveredBeansXmlUrls);
         }
         zip.close();
      }
      catch (ZipException e)
      {
         throw new RuntimeException("Error handling file " + file, e);
      }
   }

   protected void handleDirectory(File file, String path, List<String> discoveredClasses,
            List<URL> discoveredBeansXmlUrls)
   {
      handleDirectory(file, path, new File[0], discoveredClasses, discoveredBeansXmlUrls);
   }

   private void handleDirectory(File file, String path, File[] excludedDirectories, List<String> discoveredClasses,
            List<URL> discoveredBeansXmlUrls)
   {
      for (File excludedDirectory : excludedDirectories)
      {
         if (file.equals(excludedDirectory))
         {
            log.log(Level.FINEST, "Skipping excluded directory: " + file);

            return;
         }
      }

      log.log(Level.FINEST, "Handling directory: " + file);

      for (File child : file.listFiles())
      {
         String newPath = (path == null) ? child.getName() : (path + '/' + child.getName());

         if (child.isDirectory())
         {
            handleDirectory(child, newPath, excludedDirectories, discoveredClasses, discoveredBeansXmlUrls);
         }
         else
         {
            try
            {
               handle(newPath, child.toURI().toURL(), discoveredClasses, discoveredBeansXmlUrls);
            }
            catch (MalformedURLException e)
            {
               log.log(Level.SEVERE, "Error loading file: " + newPath, e);
            }
         }
      }
   }

   protected void handle(String name, URL url, List<String> discoveredClasses, List<URL> discoveredBeansXmlUrls)
   {
      if (name.endsWith(".class"))
      {
         String className = filenameToClassname(name);
         discoveredClasses.add(className);
      }
      else if (name.endsWith("beans.xml"))
      {
         discoveredBeansXmlUrls.add(url);
      }
   }

   /**
    * Convert a path to a class file to a class name
    */
   public static String filenameToClassname(String filename)
   {
      return filename.substring(0, filename.lastIndexOf(".class")).replace('/', '.').replace('\\', '.');
   }
}
