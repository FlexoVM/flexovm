/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.robovm.llvm.binding;

public enum IntPredicate {
  IntEQ(32),
  IntNE,
  IntUGT,
  IntUGE,
  IntULT,
  IntULE,
  IntSGT,
  IntSGE,
  IntSLT,
  IntSLE;

  public final int swigValue() {
    return swigValue;
  }

  public static IntPredicate swigToEnum(int swigValue) {
    IntPredicate[] swigValues = IntPredicate.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (IntPredicate swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + IntPredicate.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private IntPredicate() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private IntPredicate(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private IntPredicate(IntPredicate swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

