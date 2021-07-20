package com.hanwhalife.nbm.util;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderSupport;

public final class PropertyFactory extends PropertiesLoaderSupport {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PropertyFactory.class);

  private static Properties properties = new Properties();

  static {
    try {
      String commonPath = "classpath:com.hanwhalife.nbm.service.properties";
      Resource[] commonResources = new PathMatchingResourcePatternResolver().getResources(commonPath);
      Properties currProps;
      if (commonResources != null) {
        for (Resource resource : commonResources) {
          currProps = new Properties();
          InputStream is = resource.getInputStream();
          currProps.load(is);
          properties.putAll(currProps);
          is.close();
        }
      }
      
      commonPath = "classpath:com.hanwhalife.nbm.xtorm.properties";
      commonResources = new PathMatchingResourcePatternResolver().getResources(commonPath);
      
      if (commonResources != null) {
        for (Resource resource : commonResources) {
          currProps = new Properties();
          InputStream is = resource.getInputStream();
          currProps.load(is);
          properties.putAll(currProps);
          is.close();
        }
      }
      
    } catch (Exception ex) {
      LOGGER.error("[ERROR] ", ex);
    }
  }
  
  /**
   * CPSP 대응
   */
  private PropertyFactory() {
    
  }

  public static String getProperty(final String key) {
    return properties.getProperty(key);
  }

  public static Properties getProperties() {
    return properties;
  }

  public static void setProperty(final String key, final String value) {
    properties.setProperty(key, value);
  }
}
