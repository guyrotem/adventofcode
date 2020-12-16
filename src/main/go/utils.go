package main

import "strconv"

func filter(ss []string, test func(string) bool) (ret []string) {
	for _, s := range ss {
		if test(s) {
			ret = append(ret, s)
		}
	}
	return
}

func parseArrayToInt(input []string) []int {
	var inputNums []int

	for _, line := range input {
		v, _ := strconv.ParseInt(line, 10, 32)
		inputNums = append(inputNums, int(v))
	}
	return inputNums
}

func mustParseInt(s string) int {
	r, err := strconv.ParseInt(s, 10, 32)
	if err != nil {
		panic(err)
	}
	return int(r)
}

func mustParseInt64(s string, base int) int64 {
	r, err := strconv.ParseInt(s, base, 64)
	if err != nil {
		panic(err)
	}
	return r
}

//	TODO
type Permutable interface{}

func permutations(rules [][]Permutable) [][]Permutable {
	if len(rules) == 0 {
		return [][]Permutable{{}}
	}
	prev := permutations(rules[1:])
	ret := make([][]Permutable, len(prev)*len(rules[0]))

	for x1, perm := range prev {
		for x2, item := range rules[0] {
			ret[x1*len(rules[0])+x2] = append(perm, item)
		}
	}

	return ret
}
