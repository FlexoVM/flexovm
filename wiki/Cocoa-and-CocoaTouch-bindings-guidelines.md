Instead of being a straight port we will try to Javafy the Cocoa and CocoaTouch APIs when creating these bindings. 

# Naming of properties
We follow the Java beans property naming conventions. The getter has the prefix "is" if it returns a boolean or "get" for other return types. The setter has the prefix "set".

# Global values and constants
Use the original name but drop the initial framework prefix (`NS`, `kNS`, `UI`, etc). Global values and constants should be placed in a class having the same name as the framework, e.g. `UIKit` for the UIKit framework.

# Functions
Global C functions should be assigned to an appropriate class in the Java code. If function is documented along with a particular class we should opt for that class. If it is defined in e.g. `NSDate.h`, it should be added to the `NSDate` class. Otherwise we have to make a decision on the most appropriate class. As a last resort it can be placed in the framework class, e.g. `Foundation` for the Foundation framework.

The name of the method corresponding to the function should be trimmed of any framework prefix (`NS`, `CG`, `UI`, etc).

# Methods

## Method naming
The names of Objective-C selectors can be very long and we need to shorten them. The general rule is to keep everything up to any "With", "For", "To" and similar. Method names should also start with a verb if possible. Examples:

```
writeToFile:atomically: => write(...)
```

## Init methods and factory methods
It seems to be common in Apple's APIs to have factory-like class methods corresponding to each `init...` method, e.g.:

```
+ (instancetype)arrayWithArray:(NSArray *)anArray
- (instancetype)initWithArray:(NSArray *)anArray
```

We should opt for the `init...` methods and convert into Java constructors.

In some cases we should however use the factory methods if the method can fail with a `null` result. E.g.:

```
+ (id)arrayWithContentsOfFile:(NSString *)aPath
```

Factory methods should be called just `create` or have `create` as prefix.

# Types

## Generic collection classes
Try to determine the actual types in generic collection classes like `NSArray`, `NSDictionary` and `NSSet` and specify the type of the collection elements in Java.

## Use String rather than NSString
We return `java.lang.String` instead of `NSString` in most places. However, we should opt for `NSString` for global values and properties that or more or less opaque and only used as keys or values in dictionaries.