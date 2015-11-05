If you use a recent enough version of RoboVM, your crash logs in Xcode Organizer/Device Manager should be symbolicated automatically for you, as RoboVM places the files required for symbolication in a path that is accessible for Xcode's `symbolicatecrash`. If you use a older version of RoboVM or this does not work for you, here is how to symbolicate crash logs manually.

First we need to locate the `symbolicatecrash` executable:

```
find `xcode-select -print-path` -name symbolicatecrash -type f
```

On my system this prints out:

```
/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS.platform/Developer/Library/PrivateFrameworks/DTDeviceKitBase.framework/Versions/A/Resources/symbolicatecrash
```

But it may differ, e.g. in Xcode 6.1:
```
/Applications/Xcode.app/Contents/SharedFrameworks/DTDeviceKitBase.framework/Versions/A/Resources/symbolicatecrash
```

Let's make an alias for that so that we can just call `symbolicatecrash` without that long path. If you need to run this tool often you can add this alias to your `$HOME/.profile` file.

```
alias symbolicatecrash='/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS.platform/Developer/Library/PrivateFrameworks/DTDeviceKitBase.framework/Versions/A/Resources/symbolicatecrash'
```

The `symbolicatecrash` needs the `DEVELOPER_DIR` environment variable to be set:

```
export DEVELOPER_DIR=`xcode-select -print-path`
```

Now connect your device, open Xcode and select Window -> Devices -> (Your device). Click the "View device logs" button, select the log and use the Export option from the context menu (right-click the log entry in the list of logs on the left).

A `.crash` file will be created. To run `symbolicatecrash` we also need the path to the `.app` and `.dsym` folders of our application. If you use the Eclipse plugin these will be located in the `.metadata/.plugins/org.robovm.eclipse.ui/build` folder in your Eclipse workspace. If you use the Maven plugin you should have the folders in the `target/` folder. If the crash report comes from iTunesConnect you should have the `.app` folder in the folder where you created the `.IPA` file.

**Note:** Newer versions of RoboVM use a ramdisk setup to speed up the build, which means your build data may instead be located somewhere like `/Volumes/RoboVM RAM Disk/tmp/path/to/your/workspace/.metadata/.plugins/org.robovm.eclipse.ui/build/`.

Copy these files/folders somewhere where the symbolication script can find them. (It has to be findable via mdfind, so not in hidden folders like Eclipse's .metadata, and somewhere where your user has access rights.)

Checking this is pretty easy, as the symbolication script simply searches for the files by UUID, which you can find out by using dwarfdump on your .dsym:

```
dwarfdump --uuid example.app.dSYM
```

which outputs something like:

```
UUID: AA5E633E-FDA8-346C-AB92-B01320043DC3 (armv7) example.app.dSYM/Contents/Resources/DWARF/example
```

Now you can test for this UUID as follows:

```
mdfind "com_apple_xcode_dsym_uuids == AA5E633E-FDA8-346C-AB92-B01320043DC3"
```

Note: You can use this to find files for any UUID:
```
mdfind "com_apple_xcode_dsym_uuids == *"
```

If this finds your files, symbolicatecrash will too (if you run it with the same user/permissions).

Having the `.crash` file and the `.app` and `.dsym` folders we can now run `symbolicatecrash`:

```
symbolicatecrash "MyApp 2014-02-13 07-15.crash"
```

You can output the symbolicated crash report by piping it with the `>` operator as follows:

```Shell
symbolicatecrash "MyApp 2014-02-13 07-15.crash" > "MyApp 2014-02-13 07-15-symbolicated.crash"
```

And finally, for debugging, you can log symbolicatecrash's error output, which is very informative on any issues (note the -v parameter and the "2> symbolicate.log" to pipe stderr to symbolicate.log):
```Shell
symbolicatecrash -v "MyApp 2014-02-13 07-15.crash" > "MyApp 2014-02-13 07-15-symbolicated.crash" 2> "symbolicate.log"
```

For more information, see these resources:
* [this StackOverflow post, which can be credited for a lot of this information](http://stackoverflow.com/questions/1460892/symbolicating-iphone-app-crash-reports)
* [agentsim's Symbolicator project on GitHub, aiming for drag-and-drop symbolication support](https://github.com/agentsim/Symbolicator)