# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.23

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /snap/cmake/1082/bin/cmake

# The command to remove a file.
RM = /snap/cmake/1082/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/orion/Desktop/code/kahypar

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/orion/Desktop/code/kahypar/build

# Include any dependencies generated for this target.
include tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/compiler_depend.make

# Include the progress variables for this target.
include tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/progress.make

# Include the compile flags for this target's objects.
include tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/flags.make

tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.o: tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/flags.make
tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.o: ../tests/partition/refinement/quotient_graph_block_scheduler_test.cc
tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.o: tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/orion/Desktop/code/kahypar/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.o"
	cd /home/orion/Desktop/code/kahypar/build/tests/partition/refinement && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.o -MF CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.o.d -o CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.o -c /home/orion/Desktop/code/kahypar/tests/partition/refinement/quotient_graph_block_scheduler_test.cc

tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.i"
	cd /home/orion/Desktop/code/kahypar/build/tests/partition/refinement && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/orion/Desktop/code/kahypar/tests/partition/refinement/quotient_graph_block_scheduler_test.cc > CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.i

tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.s"
	cd /home/orion/Desktop/code/kahypar/build/tests/partition/refinement && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/orion/Desktop/code/kahypar/tests/partition/refinement/quotient_graph_block_scheduler_test.cc -o CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.s

# Object files for target quotient_graph_block_scheduler_test
quotient_graph_block_scheduler_test_OBJECTS = \
"CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.o"

# External object files for target quotient_graph_block_scheduler_test
quotient_graph_block_scheduler_test_EXTERNAL_OBJECTS =

tests/partition/refinement/quotient_graph_block_scheduler_test: tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/quotient_graph_block_scheduler_test.cc.o
tests/partition/refinement/quotient_graph_block_scheduler_test: tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/build.make
tests/partition/refinement/quotient_graph_block_scheduler_test: external_tools/googletest/googlemock/gtest/libgtest.a
tests/partition/refinement/quotient_graph_block_scheduler_test: external_tools/googletest/googlemock/gtest/libgtest_main.a
tests/partition/refinement/quotient_graph_block_scheduler_test: external_tools/googletest/googlemock/gtest/libgtest.a
tests/partition/refinement/quotient_graph_block_scheduler_test: tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/orion/Desktop/code/kahypar/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable quotient_graph_block_scheduler_test"
	cd /home/orion/Desktop/code/kahypar/build/tests/partition/refinement && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/quotient_graph_block_scheduler_test.dir/link.txt --verbose=$(VERBOSE)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --blue --bold "Running quotient_graph_block_scheduler_test"
	cd /home/orion/Desktop/code/kahypar/build/tests/partition/refinement && /home/orion/Desktop/code/kahypar/build/tests/partition/refinement/quotient_graph_block_scheduler_test

# Rule to build all files generated by this target.
tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/build: tests/partition/refinement/quotient_graph_block_scheduler_test
.PHONY : tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/build

tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/clean:
	cd /home/orion/Desktop/code/kahypar/build/tests/partition/refinement && $(CMAKE_COMMAND) -P CMakeFiles/quotient_graph_block_scheduler_test.dir/cmake_clean.cmake
.PHONY : tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/clean

tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/depend:
	cd /home/orion/Desktop/code/kahypar/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/orion/Desktop/code/kahypar /home/orion/Desktop/code/kahypar/tests/partition/refinement /home/orion/Desktop/code/kahypar/build /home/orion/Desktop/code/kahypar/build/tests/partition/refinement /home/orion/Desktop/code/kahypar/build/tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : tests/partition/refinement/CMakeFiles/quotient_graph_block_scheduler_test.dir/depend

