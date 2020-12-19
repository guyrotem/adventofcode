package main

import (
	"fmt"
	"strings"
)

func day19() {
	input := strings.Split(MustReadFile("../inputs/2020/19.txt"), "\n\n")

	rulesRaw := strings.Split(input[0], "\n")
	items := strings.Split(input[1], "\n")

	rules := make(map[int]Rule19, len(rulesRaw))
	for _, ruleRaw := range rulesRaw {
		rule := parseRule19(ruleRaw)
		rules[rule.index] = rule
	}

	countA := countMatches(rules, items)

	//	part 2
	rules[8] = Rule19{
		index: 8,
		branches: [][]string{
			{"42"},
			{"42", "8"},
		},
	}

	rules[11] = Rule19{
		index: 11,
		branches: [][]string{
			{"42", "31"},
			{"42", "11", "31"},
		},
	}
	countB := countMatches(rules, items)
	fmt.Println(countA) //	216
	fmt.Println(countB) //	400
}

func countMatches(rules map[int]Rule19, items []string) int {
	cache = make(map[int]map[string]bool)
	trees := make(map[int]TreeNode19)
	for _, rule := range rules {
		trees[rule.index] = ruleAsTree19(rule)
	}

	count := 0
	for _, item := range items {
		if ruleMatches19(trees, item, 0) {
			count++
		}
	}
	return count
}

var cache map[int]map[string]bool

func memoize19(idx int, item string, val bool) {
	if _, ok := cache[idx]; !ok {
		cache[idx] = make(map[string]bool)
	}
	cache[idx][item] = val
}

func ruleMatches19(trees map[int]TreeNode19, item string, treeIdx int) bool {
	if result, alreadyCached := cache[treeIdx][item]; alreadyCached {
		return result
	} else if trees[treeIdx].leafValue != "" {
		return trees[treeIdx].leafValue == item
	}

	found := false
	for _, opt := range trees[treeIdx].branches {
		found = calcForBranch19(trees, item, opt)
		if found {
			break
		}
	}
	memoize19(treeIdx, item, found)
	return found
}

func calcForBranch19(trees map[int]TreeNode19, item string, sections []int) bool {
	if len(sections) == 0 {
		return len(item) == 0
	} else {
		for substringIdx := range item {
			if ruleMatches19(trees, item[:substringIdx+1], sections[0]) &&
				calcForBranch19(trees, item[substringIdx+1:], sections[1:]) {
				return true
			}
		}
	}
	return false
}

type Rule19 struct {
	index    int
	branches [][]string
}

type TreeNode19 struct {
	index     int
	leafValue string
	branches  [][]int
}

func parseRule19(ruleStr string) Rule19 {
	parse1 := strings.Split(ruleStr, ": ")
	index := mustParseInt(parse1[0])
	options := strings.Split(parse1[1], " | ")
	matching := make([][]string, len(options))
	for idx, v := range options {
		matching[idx] = strings.Split(v, " ")
	}

	return Rule19{
		index:    index,
		branches: matching,
	}
}

func ruleAsTree19(rule Rule19) TreeNode19 {
	if rule.branches[0][0] == "\"a\"" || rule.branches[0][0] == "\"b\"" {
		return TreeNode19{
			index:     rule.index,
			leafValue: string(rule.branches[0][0][1]),
		}
	} else {
		branches := make([][]int, len(rule.branches))
		for branchIdx, branch := range rule.branches {
			branches[branchIdx] = []int{}
			for _, sectionTreeIndex := range branch {
				branches[branchIdx] = append(branches[branchIdx], mustParseInt(sectionTreeIndex))
			}
		}
		return TreeNode19{
			index:    rule.index,
			branches: branches,
		}
	}
}
