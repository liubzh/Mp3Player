# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             mad

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/libmad/version.c
             src/main/cpp/libmad/fixed.c
             src/main/cpp/libmad/bit.c
             src/main/cpp/libmad/timer.c
             src/main/cpp/libmad/stream.c
             src/main/cpp/libmad/frame.c
             src/main/cpp/libmad/synth.c
             src/main/cpp/libmad/decoder.c
             src/main/cpp/libmad/layer12.c
             src/main/cpp/libmad/layer3.c
             src/main/cpp/libmad/huffman.c
             src/main/cpp/libmad/FileSystem.c
             src/main/cpp/libmad/NativeMP3Decoder.c
             src/main/cpp/native_libmad.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

include_directories( src/main/cpp/libmad/android/ )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       mad

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )