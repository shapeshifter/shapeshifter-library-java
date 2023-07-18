// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.crypto;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.utils.Base64MessageEncoder;
import com.goterl.lazysodium.utils.LibraryLoader;
import com.goterl.lazysodium.utils.LibraryLoader.Mode;
import com.goterl.resourceloader.ResourceLoaderException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.extern.apachecommons.CommonsLog;
import org.lfenergy.shapeshifter.core.common.collection.AbstractInstancePool;

@CommonsLog
public class LazySodiumBase64Pool extends AbstractInstancePool<LazySodiumJava> {

  private static String extractedBundledSodiumAbsolutePath;

  @Override
  protected synchronized LazySodiumJava create() {
    return new LazySodiumJava(loadSodiumJava(), new Base64MessageEncoder());
  }

  private static SodiumJava loadSodiumJava() {
    try {
      // Standard loading mechanism
      return new SodiumJava(Mode.PREFER_SYSTEM);
    } catch (ResourceLoaderException e) {
      log.warn("Unable to load Libsodium using standard mechanism. Falling back to extraction of bundled Libsodium from classpath", e);
      return loadBundledSodiumJavaFromNestedJAR();
    }
  }

  private static SodiumJava loadBundledSodiumJavaFromNestedJAR() {
    if (extractedBundledSodiumAbsolutePath == null) {
      extractedBundledSodiumAbsolutePath = extractBundledSodiumFromNestedJAR();
    }
    return new SodiumJava(extractedBundledSodiumAbsolutePath);
  }

  /**
   * Extract bundled Sodium from nested JAR (created by Spring Boot Maven plugin). This is not yet supported by current version of LazySodium library version.
   *
   * <p>See <a href="https://github.com/terl/lazysodium-java/issues/73">Resource Loading Error With Fat Jar #73</a>.</p>
   */
  private static String extractBundledSodiumFromNestedJAR() {
    var path = '/' + LibraryLoader.getSodiumPathInResources();
    log.debug(String.format("Extracting bundled Libsodium: %s", path));

    try (var is = LibraryLoader.class.getResourceAsStream(path)) {
      if (is == null) {
        throw new IllegalStateException("Bundled Libsodium not found on classpath: " + path);
      }

      var extractedPath = Files.createTempFile("libsodium", null);
      Files.copy(is, extractedPath, StandardCopyOption.REPLACE_EXISTING);

      return extractedPath.toAbsolutePath().toString();
    } catch (IOException e) {
      throw new IllegalStateException("Could not extract bundled Sodium", e);
    }
  }
}
