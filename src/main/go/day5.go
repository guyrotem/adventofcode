package main

import (
	"fmt"
	"sort"
	"strconv"
	"strings"
)

func day5() {
	input := strings.Split(MustReadFile("../inputs/2020/5.txt"), "\n")

	type Ticket struct {
		line int
		seat int
	}

	var tickets []Ticket

	for _, row := range input {
		if len(row) < 10 {
			fmt.Println(`Invalid row: ` + row)
			return
		}
		line := strings.ReplaceAll(strings.ReplaceAll(row[0:7], "F", "0"), "B", "1")
		seat := strings.ReplaceAll(strings.ReplaceAll(row[7:10], "L", "0"), "R", "1")

		lineNum, err := strconv.ParseInt(line, 2, 32)
		if err != nil {
			fmt.Println("Invalid input")
			return
		}
		seatNum, err := strconv.ParseInt(seat, 2, 32)
		if err != nil {
			fmt.Println("Invalid input")
			return
		}

		tickets = append(tickets, Ticket{line: int(lineNum), seat: int(seatNum)})
	}

	sort.Slice(tickets, func(a, b int) bool {
		return tickets[a].line*8+tickets[a].seat > tickets[b].line*8+tickets[b].seat
	})

	var seatIds []int

	for _, ticket := range tickets {
		seatIds = append(seatIds, ticket.line*8+ticket.seat)
	}

	fmt.Println(seatIds[0]) //	813

	fmt.Println(continuityBreakDescending(seatIds)) //	612
}

func continuityBreakDescending(arr []int) int {
	for idx, v := range arr {
		if idx+1 < len(arr) && arr[idx+1] != v-1 {
			return v - 1
		}
	}
	return -1
}
