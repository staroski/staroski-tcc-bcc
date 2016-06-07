# Microsoft Developer Studio Generated NMAKE File, Based on bluecove.dsp
!IF "$(CFG)" == ""
CFG=bluecove - Win32 Release
!MESSAGE No configuration specified. Defaulting to BLUECOVE - WIN32 RELEASE.
!ENDIF

!IF "$(CFG)" != "bluecove - Win32 Release"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE
!MESSAGE NMAKE /f "bluecove.mak" CFG="bluecove - Win32 Release"
!MESSAGE
!MESSAGE Possible choices for configuration are:
!MESSAGE
!MESSAGE "bluecove - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE
!ERROR An invalid configuration is specified.
!ENDIF

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE
NULL=nul
!ENDIF

OUTDIR=.\Win32\bluecove
INTDIR=.\Win32\bluecove
# Begin Custom Macros
OutDir=.\Win32\bluecove
# End Custom Macros

ALL : "..\..\resources\bluecove.dll" "$(OUTDIR)\bluecove.bsc"


CLEAN :
	-@erase "$(INTDIR)\bluecove.res"
	-@erase "$(INTDIR)\BlueSoleilStack.obj"
	-@erase "$(INTDIR)\BlueSoleilStack.sbr"
	-@erase "$(INTDIR)\common.obj"
	-@erase "$(INTDIR)\common.sbr"
	-@erase "$(INTDIR)\commonTest.obj"
	-@erase "$(INTDIR)\commonTest.sbr"
	-@erase "$(INTDIR)\ToshibaStack.obj"
	-@erase "$(INTDIR)\ToshibaStack.sbr"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\WIDCOMMStack.obj"
	-@erase "$(INTDIR)\WIDCOMMStack.sbr"
	-@erase "$(INTDIR)\WIDCOMMStackRFCOMM.obj"
	-@erase "$(INTDIR)\WIDCOMMStackRFCOMM.sbr"
	-@erase "$(INTDIR)\WIDCOMMStackL2CAP.obj"
	-@erase "$(INTDIR)\WIDCOMMStackL2CAP.sbr"
	-@erase "$(OUTDIR)\bluecove.bsc"
	-@erase "$(OUTDIR)\bluecove.exp"
	-@erase "$(OUTDIR)\bluecove.lib"
	-@erase "..\..\resources\bluecove.dll"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MD /W3 /GX /O2 /I "$(JAVA_HOME)\include" /I "$(JAVA_HOME)\include\win32" /I "$(ProgramFiles)\Widcomm\BTW DK\SDK\Inc" /I "$(ProgramFiles)\IVT Corporation\BlueSoleil\api" /I "$(ProgramFiles)\Toshiba\Bluetooth Toshiba Stack\SDK\include" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "UNICODE" /D "BLUECOVE_EXPORTS" /D "_BTWLIB" /D "xBLUECOVE_TOSHIBA" /FR"$(INTDIR)\\" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $<
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $<
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $<
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $<
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $<
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $<
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "NDEBUG" /mktyplib203 /win32
RSC=rc.exe
RSC_PROJ=/l 0x1009 /fo"$(INTDIR)\bluecove.res" /d "NDEBUG"
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\bluecove.bsc"
BSC32_SBRS= \
	"$(INTDIR)\BlueSoleilStack.sbr" \
	"$(INTDIR)\common.sbr" \
	"$(INTDIR)\commonTest.sbr" \
	"$(INTDIR)\WIDCOMMStack.sbr" \
	"$(INTDIR)\WIDCOMMStackRFCOMM.sbr" \
	"$(INTDIR)\WIDCOMMStackL2CAP.sbr" \
	"$(INTDIR)\ToshibaStack.sbr"

"$(OUTDIR)\bluecove.bsc" : "$(OUTDIR)" $(BSC32_SBRS)
    $(BSC32) @<<
  $(BSC32_FLAGS) $(BSC32_SBRS)
<<

LINK32=link.exe
LINK32_FLAGS=ws2_32.lib version.lib advapi32.lib user32.lib winspool.lib ole32.lib oleaut32.lib /nologo /dll /incremental:no /pdb:"$(OUTDIR)\bluecove.pdb" /machine:I386 /out:"..\..\resources\bluecove.dll" /implib:"$(OUTDIR)\bluecove.lib" /libpath:"$(ProgramFiles)\Widcomm\BTW DK\SDK\Release" /libpath:"$(ProgramFiles)\IVT Corporation\BlueSoleil\api" /libpath:"$(ProgramFiles)\Toshiba\Bluetooth Toshiba Stack\SDK\lib"
LINK32_OBJS= \
	"$(INTDIR)\BlueSoleilStack.obj" \
	"$(INTDIR)\common.obj" \
	"$(INTDIR)\commonTest.obj" \
	"$(INTDIR)\WIDCOMMStack.obj" \
	"$(INTDIR)\WIDCOMMStackRFCOMM.obj" \
	"$(INTDIR)\WIDCOMMStackL2CAP.obj" \
	"$(INTDIR)\bluecove.res" \
	"$(INTDIR)\ToshibaStack.obj"

"..\..\resources\bluecove.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<


!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("bluecove.dep")
!INCLUDE "bluecove.dep"
!ELSE
#!MESSAGE Warning: cannot find "bluecove.dep"
!ENDIF
!ENDIF


!IF "$(CFG)" == "bluecove - Win32 Release"
SOURCE=.\BlueSoleilStack.cpp

"$(INTDIR)\BlueSoleilStack.obj"	"$(INTDIR)\BlueSoleilStack.sbr" : $(SOURCE) "$(INTDIR)"


SOURCE=..\..\..\..\target\classes\com\intel\bluetooth\BluetoothStackWIDCOMM.class
InputPath=..\..\..\..\target\classes\com\intel\bluetooth\BluetoothStackWIDCOMM.class

".\com_intel_bluetooth_BluetoothStackWIDCOMM.h" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	<<tempfile.bat
	@echo off
	javah -jni -classpath ..\..\..\..\target\classes com.intel.bluetooth.BluetoothStackWIDCOMM
<<

SOURCE=.\common.cpp

"$(INTDIR)\common.obj"	"$(INTDIR)\common.sbr" : $(SOURCE) "$(INTDIR)"


SOURCE=.\commonTest.cpp

"$(INTDIR)\commonTest.obj"	"$(INTDIR)\commonTest.sbr" : $(SOURCE) "$(INTDIR)"


SOURCE=.\ToshibaStack.cpp

"$(INTDIR)\ToshibaStack.obj"	"$(INTDIR)\ToshibaStack.sbr" : $(SOURCE) "$(INTDIR)"


SOURCE=.\WIDCOMMStack.cpp

"$(INTDIR)\WIDCOMMStack.obj"	"$(INTDIR)\WIDCOMMStack.sbr" : $(SOURCE) "$(INTDIR)" ".\com_intel_bluetooth_BluetoothStackWIDCOMM.h"

SOURCE=.\WIDCOMMStackRFCOMM.cpp

"$(INTDIR)\WIDCOMMStackRFCOMM.obj"	"$(INTDIR)\WIDCOMMStackRFCOMM.sbr" : $(SOURCE) "$(INTDIR)" ".\com_intel_bluetooth_BluetoothStackWIDCOMM.h"

SOURCE=.\WIDCOMMStackL2CAP.cpp

"$(INTDIR)\WIDCOMMStackL2CAP.obj"	"$(INTDIR)\WIDCOMMStackL2CAP.sbr" : $(SOURCE) "$(INTDIR)" ".\com_intel_bluetooth_BluetoothStackWIDCOMM.h"


SOURCE=.\bluecove.rc

"$(INTDIR)\bluecove.res" : $(SOURCE) "$(INTDIR)"
	$(RSC) $(RSC_PROJ) $(SOURCE)



!ENDIF

