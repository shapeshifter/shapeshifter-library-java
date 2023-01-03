package org.lfenergy.shapeshifter.connector.application;

import java.io.IOException;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

public class ExcludeTestMappingFilter implements TypeFilter {

  @Override
  public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
    return metadataReader.getResource()
                         .getFile().getPath()
                         .contains("testmapping");
  }
}
