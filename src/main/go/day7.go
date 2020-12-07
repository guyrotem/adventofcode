package main

import (
	"fmt"
	"regexp"
	"strconv"
	"strings"
)

func day7() {
	input := strings.Split(MustReadFile("../inputs/2020/7.txt"), "\n")
	bags := parse(input)

	fmt.Println(bags)

	initialColor := "shiny gold"

	total := make(map[string]bool)
	prev := make(map[string]bool)

	prev[initialColor] = true

	for len(prev) > 0 {
		for k, v := range prev {
			total[k] = v
		}
		prev = findContainingBags(prev, bags)
	}

	fmt.Println(len(total) - 1) //	259

	bagsMap := make(map[string]BagInfo)
	for _, bag := range bags {
		bagsMap[bag.color] = bag
	}
	fmt.Println(rank(initialColor, bagsMap) - 1) //45018

}

func findContainingBags(lookupColors map[string]bool, allBags []BagInfo) map[string]bool {
	var result = make(map[string]bool)

	for _, bagInfo := range allBags {
		for _, bagInside := range bagInfo.contents {
			if _, found := lookupColors[bagInside.color]; found {
				result[bagInfo.color] = true
				break
			}
		}
	}
	return result
}

func rank(color string, allBags map[string]BagInfo) int {
	res := 1
	for _, bagAmount := range allBags[color].contents {
		res += bagAmount.count * rank(bagAmount.color, allBags)
	}
	return res
}

type BagInfo struct {
	color    string
	contents []BagAmount
}

type BagAmount struct {
	color string
	count int
}

func parse(lines []string) []BagInfo {
	bagRegex := regexp.MustCompile("^([0-9]*) ([a-z][a-z ]*) bag[\\\\s]?$")
	var bags []BagInfo

	for _, line := range lines {
		parsedLine := strings.Split(line, " bags contain ")
		containerColor := parsedLine[0]
		rawBags := parsedLine[1]
		var contents []BagAmount
		if rawBags != "no other bags." {
			for _, rawBag := range strings.Split(rawBags[:len(rawBags)-1], ", ") {
				contentArray := bagRegex.FindStringSubmatch(rawBag)
				count, err := strconv.ParseInt(contentArray[1], 10, 32)
				if err != nil {
					panic(err)
				}
				color := contentArray[2]

				contents = append(contents, BagAmount{
					color: color,
					count: int(count),
				})
			}
		}
		bags = append(bags, BagInfo{
			color:    containerColor,
			contents: contents,
		})
	}

	return bags
}
