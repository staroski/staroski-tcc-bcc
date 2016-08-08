========================================================================
    DYNAMIC LINK LIBRARY : intelbth.dll and bluecove.dll
========================================================================


intelbth.vcproj
    This is the project file for VC 2005 project.

    There are four Configurations:  Winsock, WIDCOMM, BlueSoleil, All and Release

    Release Configuration on Win32 will incorporated Winsock and BlueSoleil Configurations and should be used for distribution!

    N.B. We can't use the same DLL on windows for all implemenations. Since WIDCOMM need to be compile /MD using VC6 and winsock and BlueSoleil /MT using VC2005
        intelbth.dll build by VC2005 Configuration "Win32 Release"
        bluecove.dll build by VC6 Configuration "Win32 Release"

    "Pocket PC 2003 (ARMV4)" is Platform we use to build DLL for WindowsCE
    Build for Windows Mobile will use Winsock and WIDCOMM Configurations
        intelbth_ce.dll build by VC2005 Configuration "Pocket PC 2003 (ARMV4) Winsock"
        bluecove_ce.dll build by VC2005 Configuration "Pocket PC 2003 (ARMV4) WIDCOMM"


    There are three working Configurations:  Winsock, WIDCOMM and Release  TODO

    A precompiled header (StdAfx.h, StdAfx.cpp) can't be used because BlueSoleil redefine some Microsoft definitions from BluetoothAPIs.h

    Runtime Library Multi-threaded static (/MT) used for intelbth.dll to avoid dependancy on MSVCR80.DLL

    Runtime Library Multi-threaded DLL (/MD) used for bluecove.dll and build by VC6.


WIDCOMM:  bluecove.dll
    Get Broadcom development kits from:  http://www.broadcom.com/products/bluetooth_sdk.php
    You have to register at the Broadcom site to gain access to the downloads.
    We are using BTW-6_1_0_1502 for destribution.
    Install it to default directory. e.g. "$(ProgramFiles)\Widcomm\BTW DK" for Win32
    Make all the files read only if you will ever uninstall WIDCOMM drivers the SDK files should remain.
    You don't need to have bluetooth WIDCOMM drivers installed to build the dll.

WIDCOMM Windows Mobile:  bluecove_ce.dll
    Get Broadcom development kits from:  http://www.broadcom.com/products/bluetooth_sdk.php
    BTW-CE SDK  Version 1.7.1.5201
    Install it to default directory. e.g. "$(ProgramFiles)\Widcomm\WIDCOMM BTW-CE SDK"

BlueSoleil:  intelbth.dll
    Get BlueSoleil� PC Platform Software Development Kit (SDK), 0.83 free version from this location:
    http://www.bluesoleil.com/download/index.asp?topic=bluesoleil_sdk

    The BlueSoleil API should be installed in directory: $(ProgramFiles)\IVT Corporation\BlueSoleil\api
    Make all the files read only if you will ever uninstall BlueSoleil the SDK files should remain.

    Four files should be there: a DLL file btfunc.dll, a library file btfunc.lib and two header file bt_ui.h, bt_def.h.

    You don't need to have bluetooth BlueSoleil drivers installed to build the dll.

Toshiba:
    The Toshiba Bluetooth Stack SDK is free of charge, but requires a NDA with Toshiba. http://aps2.toshiba-tro.de/bluetooth/pages/faq/sdk.html
    The Toshiba Bluetooth SDK should be installed in directory: $(ProgramFiles)\Toshiba\Bluetooth Toshiba Stack\SDK

common.h
    Used instead of StdAfx.h

common.cpp
    This is the main DLL source file.

intelbth.cpp
    This is the source file for Winsock Stack.

WIDCOMMStack.cpp and WIDCOMMStack.h
    This is the source file for WIDCOMM Stack.

BlueSoleilStack.cpp and BlueSoleilStack.h
    This is the source file for BlueSoleil Stack.

/////////////////////////////////////////////////////////////////////////////
Other notes:

1)
   Q. Why so many if (stack == NULL) in the code?
   A. This is the case of JVM shutdown that can happen any time (User just close the boring app). We don't want to have coredum.

/////////////////////////////////////////////////////////////////////////////


