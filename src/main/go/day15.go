package main

import (
	"fmt"
	"strconv"
	"strings"
)

func day15() {
	//demoInput := strings.Split("0,3,6", ",")
	input := strings.Split("18,8,0,5,4,1,20", ",")
	lastSeen := make(map[int]int)
	var prevNum int
	for idx, numStr := range input {
		lastSeen[mustParseInt(numStr)] = idx
		prevNum = mustParseInt(numStr)
	}

	turn := len(input)
	var next int

	for turn < 30000000 {
		if lastSeenAt, ok := lastSeen[prevNum]; ok {
			next = turn - 1 - lastSeenAt
		} else {
			next = 0
		}
		lastSeen[prevNum] = turn - 1
		prevNum = next
		turn++
	}
	fmt.Println(strconv.Itoa(turn) + `: ` + strconv.Itoa(next))

}
