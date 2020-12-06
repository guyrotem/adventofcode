package main

import (
	"fmt"
	"strings"
)

func day6() {
	input := strings.Split(MustReadFile("../inputs/2020/6.txt"), "\n\n")

	union := 0
	intersection := 0

	for _, group := range input {
		charCounts := make(map[string]int)

		members := strings.Split(group, "\n")
		for _, member := range members {
			for _, answer := range strings.Split(member, "") {
				charCounts[answer] = charCounts[answer] + 1
			}
		}

		union += len(charCounts)
		for _, count := range charCounts {
			if count == len(members) {
				intersection++
			}
		}
	}

	fmt.Println(union)
	fmt.Println(intersection)

}
