/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package newshog.thrift.server;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum TweetoMeter implements org.apache.thrift.TEnum {
  Meh(0),
  Tepid(1),
  Buzzing(2),
  Hot(3),
  Blazing(4);

  private final int value;

  private TweetoMeter(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static TweetoMeter findByValue(int value) { 
    switch (value) {
      case 0:
        return Meh;
      case 1:
        return Tepid;
      case 2:
        return Buzzing;
      case 3:
        return Hot;
      case 4:
        return Blazing;
      default:
        return null;
    }
  }
}
