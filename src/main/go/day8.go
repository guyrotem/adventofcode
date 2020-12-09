package main

import (
	"fmt"
	"regexp"
	"strconv"
	"strings"
)

func day8() {
	input := strings.Split(MustReadFile("../inputs/2020/8.txt"), "\n")
	commands := parse8(input)

	state, _ := runUntilLoop(commands)
	fmt.Println(state.acc) //	1487

	_, noLoopAcc := breakLoop(commands)
	fmt.Println(noLoopAcc) //	1607
}

func breakLoop(commands []*Command) (int, int) {
	for idx, cmd := range commands {
		if cmd.action == "nop" {
			cmd.action = "jmp"
			state, _ := runUntilLoop(commands)
			if len(state.loopPointers) == 0 {
				return idx, state.acc
			}
			cmd.action = "nop"
		}
		if cmd.action == "jmp" {
			cmd.action = "nop"
			state, _ := runUntilLoop(commands)
			if len(state.loopPointers) == 0 {
				return idx, state.acc
			}
			cmd.action = "jmp"
		}
	}
	panic("all replacements ended with a loop!")
}

type Command struct {
	action string
	num    int
}

func parse8(input []string) []*Command {
	commandRegex := regexp.MustCompile("^([a-z]*) ([+-])([0-9]*)")
	var commands []*Command

	for _, line := range input {
		parsed := commandRegex.FindStringSubmatch(line)
		action := parsed[1]
		abs, err := strconv.ParseInt(parsed[3], 10, 32)
		if err != nil {
			panic(err)
		}
		var num int

		if parsed[2] == "-" {
			num = int(abs) * -1
		} else if parsed[2] == "+" {
			num = int(abs)
		} else {
			panic(`Unknown sign ` + parsed[2])
		}
		commands = append(commands, &Command{
			action: action,
			num:    num,
		})

	}
	return commands
}

type MachineState struct {
	address      int
	acc          int
	loopPointers []int
}

func runUntilLoop(commands []*Command) (MachineState, error) {
	acc := 0
	cmdPointer := 0
	visited := make(map[int]bool)
	var loop []int
	var accOnLoopStart int

	for cmdPointer < len(commands) {
		if _, exists := visited[cmdPointer]; exists {
			if len(loop) == 0 {
				accOnLoopStart = acc
			} else if loop[0] == cmdPointer {
				return MachineState{
					address:      cmdPointer,
					acc:          accOnLoopStart,
					loopPointers: loop,
				}, nil
			}
			loop = append(loop, cmdPointer)
		}
		visited[cmdPointer] = true
		cmd := commands[cmdPointer]
		if cmd.action == "jmp" {
			cmdPointer += cmd.num
		} else if cmd.action == "acc" {
			acc += cmd.num
			cmdPointer++
		} else if cmd.action == "nop" {
			cmdPointer++
		} else {
			return MachineState{}, fmt.Errorf("invalid command found")
		}
	}

	return MachineState{
		address: cmdPointer,
		acc:     acc,
	}, nil
}
