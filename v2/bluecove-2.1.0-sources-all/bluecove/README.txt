#summary BlueCove documentation.
#labels Featured

= BlueCove =

== About ==

BlueCove is a LGPL licensed JSR-82 implementation on Java Standard Edition (J2SE) that currently interfaces with
the Mac OS X, WIDCOMM, BlueSoleil and Microsoft Bluetooth stack. Originally developed by Intel Research and
currently maintained by volunteers. The LGPL license allow to link and distribute commercial software with BlueCove.

Support for Linux BlueZ is added in BlueCove version 2.0.3 as additional [http://www.gnu.org/licenses/gpl.html GNU General Public License] module `bluecove-gpl`.

JSR-82 Emulator added in BlueCove version 2.0.3 as additional module `bluecove-emu`.

 [http://bluecove.wiki.sourceforge.net/ BlueCove public contirbutions Wiki]

 [http://www.rococosoft.com/weblog/archives/2007/07/another_cool_project_using_jsr.html Blog about Blue Cove]

== Library API ===

BlueCove provides an implementation of the JSR 82. Applications should use API defined in JSR-82. See [http://www.bluecove.org/apidocs/index.html BlueCove JSR-82 API]

== Runtime Requirements ==

  * WIDCOMM (Broadcom) BTW Stack software version 1.4.2.10 SP5 or above
  * BlueSoleil version 1.6.0, 2.3 or 3.2.2.8. Version 5.0.5 not supported.
  * Microsoft Bluetooth stack (currently this means Windows XP SP2 or newer and Windows Mobile 2003 or newer)
  * PowerPC- or Intel-based Mac OS X 10.4 (Bluetooth v1.2) or late (Since v2.0.2)
  * Linux with BlueZ Bluetooth stack
  * A Bluetooth device supported by the WIDCOMM, BlueSoleil or Microsoft bluetooth stack
  * Java 1.1 or newer for the binary execution, Java 1.4 or newer to compile.
  * Another Bluetooth device to communicate with. See [http://code.google.com/p/bluecove/wiki/phones Complete list of the JSR-82 compliant phones]

== Limitations ==

  L2CAP support available only on Windows WIDCOMM Stack, Linux BlueZ and Mac OS X Stack.

  Since Microsoft Bluetooth stack only supporting RFCOMM connections,
BlueCove also supports only RFCOMM connections on this stack. The Microsoft operating system support is
currently limited to Windows XP SP2, Windows Mobile 2003 and newer, because the Microsoft Bluetooth
stack is not available on other operating systems.

 For more limitations details see stacks.txt or [http://code.google.com/p/bluecove/wiki/stacks BlueCove supported stacks].

 We hope you enjoy using BlueCove. Please note that this is an open-source effort.
If you feel the code could use new features or fixes, or the documentation can be improved, please get involved and lend us a hand!
The BlueCove developer community welcomes your participation.

== Not Implemented functionality ===

  * `RemoteDevice` authenticate, authorize and encrypt Not implemented

== Installation ==

BlueCove rely upon already installed native Bluetooth stack that comes with you operating system or Bluetooth USB device.
Installation of the binary (already compiled) version of BlueCove is as follows:

  * [http://code.google.com/p/bluecove/downloads/list Download BlueCove] binary release
  * Add `bluecove.jar` to your classpath
  * Also add `bluecove-gpl.jar` to your classpath if you are running application on Linux

For maven2 users see [maven2 Using maven2 to build application or MIDlet]

== Runtime configuration ==

Bluetooth Stack

   If automatic Bluetooth Stack detection is not enough Java System property "bluecove.stack" can be used to force desired Stack Initialization.
   Values "widcomm", "bluesoleil" or "winsock". By default winsock is selected if available.

   Another property "bluecove.stack.first" is used optimize stack detection.
   If -Dbluecove.stack.first=widcomm then widcomm (bluecove.dll) stack is loaded first and if not available then BlueCove will switch to winsock.
   By default intelbth.dll is loaded first.

   If multiple stacks are detected they are selected in following order: "winsock", "widcomm", "bluesoleil".
   Since BlueCove v2.0.1 "bluecove.stack.first" will alter the order of stack selection.

   If System property is not an option (e.g. when running in Webstart) create text file "bluecove.stack" or "bluecove.stack.first" containing stack name and add this file to BlueCove or Application jar. (Since v2.0.1)

Native Library location

  * By default Native Library is extracted from from jar to temporary directory `${java.io.tmpdir}/bluecove_${user.name}_N` and loaded from this location.
  * If you wish to load library (.dll) from another location add this system property `-Dbluecove.native.path=/your/path`.
  * If you wish to load library from default location in path e.g. `%SystemRoot%\system32` or any other location in %PATH% use `-Dbluecove.native.resource=false`

IBM J9 Personal Profile

    To run BlueCove with [http://www.ibm.com/software/wireless/weme/ IBMs J9] Java VM on Win32 or PocketPC add this system property `-Dmicroedition.connection.pkgs=com.intel.bluetooth`.

    Tested on
        * WebSphere Everyplace Micro Environment 5.7.2, CDC 1.0/Foundation 1.0/Personal Profile 1.0 for Windows XP/X86
        * WebSphere Everyplace Micro Environment 6.1.1, CDC 1.0/Foundation 1.0/Personal Profile 1.0 for Windows XP/X86 and Linux/X86

IBM J9 MIDP 2.0 Profile
    * Copy to `bluecove.jar` %J9_HOME%\lib\jclMidp20\ext directory (remove `javax.microedition.io` classes from the jar)
    * Copy all bluecove dlls (so) to %J9_HOME%\bin directory or add -Dcom.ibm.oti.vm.bootstrap.library.path=%bluecove_dll_path%;%J9_HOME%\bin
    * run app "%J9_HOME%\bin\j9.exe" -jcl:midp20 -Dmicroedition.connection.pkgs=com.intel.bluetooth -cp target\bctest.jar "-jxe:%J9_HOME%\lib\jclMidp20\jclMidp20.jxe" target\bctest.jad
    * -Dmicroedition.connection.pkgs=com.intel.bluetooth is optonal if you place bluecove.jar to ext directory (Since v2.0.2)

    Tested on
        * WebSphere Everyplace Micro Environment 5.7.2,	CLDC 1.1, MIDP 2.0 for Windows XP/X86
        * WebSphere Everyplace Micro Environment 6.1.1, CLDC 1.1, MIDP 2.0 for Windows XP/X86 and Linux/X86

SUN CDC

    * Take [http://snapshot.bluecove.org/special-build/ custom bluecove and intelbth dll build] for CDC and place in CDCTK10\bin
    * Add -Dbluecove.native.resource=false when running emulator or remove original dlls from bluecove jar
    * Run CDC emulator adding BlueCove to boot class path of emulator using -Xbootclasspath: argument

MicroEmulator with JSR-82

    * java -cp microemulator.jar;bluecove.jar org.microemu.app.Main --impl com.intel.bluetooth.BlueCoveImpl path2yourApp/YourApp.jad

Debug

    If something goes wrong system property `-Dbluecove.debug=true` will enable debug prints in BlueCove code
    If System property is not an option (e.g. when running MIDP application in emulator) create text file "bluecove.debug" containing one line 'true' and add this file to BlueCove.jar. (Since v2.0.2)

    Bluecove log is redirected to log4j when log4j.jar is available in classpath. Debug can be enabled using log4j configuration. (Since v2.0.2)

Configuration options

    System properties:
     Since v2.0.2
        * `bluecove.obex.timeout` time in milliseconds defaults to 2 minutes.
        * `bluecove.connect.timeout` time in milliseconds defaults to 2 minutes. WIDCOMM and OS X only.
        * `bluecove.obex.mtu` You can increase transfer speed by changing mtu to bigger value. Default is 1024
        * `bluecove.bluez.class` defaults to com.intel.bluetooth.BluetoothStackBlueZ. Use reflection to start Linux version.
    Since v2.0.3
        * `bluecove.inquiry.duration` Device Inquiry time in seconds defaults to 11 seconds. MS Stack and OS X only.
        * `bluecove.inquiry.report_asap` Set true to make Device Inquiry call DiscoveryListener.deviceDiscovered without waiting for updated service class.  WIDCOMM only.

== Compilation ==

On Windows

 You need a C++ compiler and JDK. Tested on Visual C++ 2005 Express Edition SP1 and SDK for Windows Vista or Windows Server 2003 R2 Platform SDK.
 VC++ and Windows SDK are available for free download from microsoft.com.
 We are using for Windows Vista SDK for binary distribution:
    Make sure you have
     Tools ->  Options -> VC++ Directories ->
        "Include files" %ProgramFiles%\Microsoft SDKs\Windows\v6.0\Include
        "Library files" %ProgramFiles%\Microsoft SDKs\Windows\v6.0\lib

 We can't use the same DLL on windows for all implemenations. Since WIDCOMM needs to be compiled /MD using VC6 and winsock /MT using VC2005
  intelbth.dll build by VC2005 Configuration "Win32 winsock"
  bluecove.dll build by VC6 Configuration "Win32 Release"

 Visual C++ 6.0 SP6 used to build bluecove.dll for WIDCOMM
 Visual Visual C++ 2005 used to build intelbth.dll for winsock and BlueSoleil
 More detail on building native code `src\main\c\intelbth\ReadMe.txt`

On Mac OS X

  Xcode 2.5 or later can be used duirng build on Mac.
  We use Xcode 3.0 on OSX 10.5.2 to build distribution.

 Ant or maven2 are used as the build tool for java.

  * [http://code.google.com/p/bluecove/downloads/list Download BlueCove]source release
  * Unzip the source
  * Run `ant` or `mvn`
  * When using maven native Dlls are build as well

  If you are using `ant`:

  * Go into `src\main\c\intelbth`
  * Open `intelbth.sln`
  * Compile the project for your platform (e.g. 'Winsock' for 'Win32')
  * Run `ant jar` or `mvn`

 We don't use `ant` for official distributions! `build.xml` only provided for your convenience

== Source ==

Available as downloadable packages or at the Subversion repository http://bluecove.googlecode.com/svn/trunk/ or for each released version http://bluecove.googlecode.com/svn/tags/.

Organized in:

  * *`src\main\c\intelbth`* - The native windows JNI dll
  * *`src\main\java`* - The implementation of JSR-82 with calls to intelbth
  * *`src\test\java`* - Some test programs
