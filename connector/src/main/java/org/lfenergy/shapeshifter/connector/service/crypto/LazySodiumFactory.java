package org.lfenergy.shapeshifter.connector.service.crypto;

import com.goterl.lazysodium.utils.Key;
import org.springframework.stereotype.Service;

@Service
public class LazySodiumFactory {

  public Key keyFromBase64String(String base64Key) {
    return Key.fromBase64String(base64Key);
  }
}
