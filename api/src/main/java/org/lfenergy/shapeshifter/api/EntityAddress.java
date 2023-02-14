package org.lfenergy.shapeshifter.api;

/**
 * Entity Address such as EAN or EA1.
 *
 * <p>From the USEF Flex Trading Protocol Specification, Section 7. EA1 addressing scheme:<br/>
 * In USEF messages, there is often a requirement for a globally unique identity for certain entities. To meet this requirement, USEF defines the Entity Address (EA).
 * </p>
 *
 * <p>Currently, two addressing schemes are supported:
 * <ul>
 * <li>The European Article Number (EAN), commonly used to uniquely identify connection points in the electricity network and therefore a natural identifier to do the same in USEF. An example of an EA using this scheme is: ean.871685900012636543</li>
 * <li>The USEF type 1 entity address (EA1) is designed to allow participants to generate unique identifiers for themselves and entities managed by them, without relying on a central authority.</li>
 * </ul>
 * </p>
 */
public record EntityAddress(Scheme scheme, String address) {

  public static EntityAddress parse(String str) {
    var parts = str.split("\\.", 2);
    if (parts.length != 2) {
      throw new IllegalArgumentException("Entity Address does not specify a scheme: " + str);
    }
    if (parts[1].isEmpty()) {
      throw new IllegalArgumentException("Entity Address has an empty address: " + str);
    }
    return new EntityAddress(Scheme.fromPrefix(parts[0]), parts[1]);
  }

  @Override
  public String toString() {
    return scheme.getPrefix() + "." + address;
  }

  public enum Scheme {
    EAN("ean"), EA1("ea1");

    private final String prefix;

    Scheme(String prefix) {
      this.prefix = prefix;
    }

    public static Scheme fromPrefix(String prefix) {
      for (var scheme : values()) {
        if (scheme.prefix.equals(prefix)) {
          return scheme;
        }
      }
      throw new IllegalArgumentException("Unsupported Entity Address scheme: " + prefix);
    }

    public String getPrefix() {
      return prefix;
    }
  }

}
