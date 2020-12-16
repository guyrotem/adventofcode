package main

import (
	"fmt"
	"regexp"
	"strconv"
	"strings"
)

func day16() {
	input := strings.Split(MustReadFile("../inputs/2020/16.txt"), "\n\n")
	rules, myTicket, nearbyTickets := parseDay16(input)

	fmt.Println(len(rules))
	fmt.Println(len(nearbyTickets))

	sum := 0
	var validTickets []Ticket
	for _, nearbyTicket := range nearbyTickets {
		ticketMatch := true
		for _, num := range nearbyTicket.nums {
			matchFound := false
			for _, rule := range rules {
				if matchRule(num, rule) {
					matchFound = true
					break
				}
			}
			if !matchFound {
				sum += num
				ticketMatch = false
			}
		}
		if ticketMatch {
			validTickets = append(validTickets, nearbyTicket)
		}
	}
	fmt.Println(sum) //26988

	candidates := make([][]OrRule, len(rules))

	for idx := range candidates {
		candidates[idx] = rules
	}

	for _, ticket := range validTickets {
		for idx, num := range ticket.nums {
			var filter []OrRule
			for _, candidate := range candidates[idx] {
				if matchRule(num, candidate) {
					filter = append(filter, candidate)
				}
			}
			candidates[idx] = filter
		}
	}

	ordered := make([]string, len(candidates))
	idx := findLenOne(calcLens(candidates))
	for idx >= 0 {
		v := candidates[idx][0].fieldName
		ordered[idx] = v
		filterItem(candidates, v)
		idx = findLenOne(calcLens(candidates))
	}
	mult := 1
	for idx, v := range ordered {
		if strings.Contains(v, "departure") {
			fmt.Println(strconv.Itoa(idx) + ` : ` + v)
			mult *= myTicket.nums[idx]
		}
	}
	fmt.Println(mult) //	426362917709
}

func parseDay16(input []string) ([]OrRule, Ticket, []Ticket) {
	rulesString := strings.Split(input[0], "\n")
	myTicket := parseTicket(strings.Split(input[1], "\n")[1])
	nearbyTicketsString := strings.Split(input[2], "\n")[1:]

	var rules []OrRule
	var nearbyTickets []Ticket

	for _, ruleString := range rulesString {
		rules = append(rules, parseRule(ruleString))
	}

	for _, nearbyTicketString := range nearbyTicketsString {
		nearbyTickets = append(nearbyTickets, parseTicket(nearbyTicketString))
	}
	return rules, myTicket, nearbyTickets
}

func findLenOne(lens []int) int {
	for idx, v := range lens {
		if v == 1 {
			return idx
		}
	}
	return -1
}

func filterItem(candidates [][]OrRule, fieldName string) {
	for idx := range candidates {
		var filter []OrRule
		for _, xxx := range candidates[idx] {
			if xxx.fieldName != fieldName {
				filter = append(filter, xxx)
			}
		}
		candidates[idx] = filter

	}
}

func calcLens(candidates [][]OrRule) []int {
	var lens []int
	for _, x := range candidates {
		lens = append(lens, len(x))
	}
	return lens
}

func matchRule(num int, rule OrRule) bool {
	return (num >= rule.range1.from && num <= rule.range1.to) || (num >= rule.range2.from && num <= rule.range2.to)
}

func parseTicket(nearbyTicketString string) Ticket {
	var nums []int
	for _, numStr := range strings.Split(nearbyTicketString, ",") {
		nums = append(nums, mustParseInt(numStr))
	}
	ticket := Ticket{
		nums: nums,
	}
	return ticket
}

type Range struct {
	from int
	to   int
}

type OrRule struct {
	fieldName string
	range1    Range
	range2    Range
}

type Ticket struct {
	nums []int
}

func parseRule(ruleString string) OrRule {
	ruleRegex := regexp.MustCompile("^([a-z ]*): ([0-9]*)-([0-9]*) or ([0-9]*)-([0-9]*)$")
	res := ruleRegex.FindStringSubmatch(ruleString)
	if len(res) != 6 {
		panic(len(res))
	}
	return OrRule{
		fieldName: res[1],
		range1: Range{
			from: mustParseInt(res[2]),
			to:   mustParseInt(res[3]),
		},
		range2: Range{
			from: mustParseInt(res[4]),
			to:   mustParseInt(res[5]),
		},
	}
}
