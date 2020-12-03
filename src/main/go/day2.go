package main

import (
	"fmt"
	"regexp"
	"strconv"
	"strings"
)

func main() {
	content, err := ReadFile("./inputs/2020/2.txt")

	if err != nil {
		fmt.Println(err)
		return
	}
	input := *content
	parsed, err := parse2(input)
	if err != nil {
		return
	}

	count1, count2 := 0, 0
	for _, row := range parsed {
		if decideRow1(row) {
			count1++
		}
		if decideRow2(row) {
			count2++
		}
	}

	fmt.Println(count1) //	666
	fmt.Println(count2) //	670

}

type Row struct {
	index1 int
	index2 int
	letter string
	query  string
}

func decideRow1(row Row) bool {
	queryLetters := strings.Split(row.query, "")
	appearancesOfLetter := len(filter(queryLetters, func(s string) bool { return s == row.letter }))
	return appearancesOfLetter >= row.index1 && appearancesOfLetter <= row.index2
}

func decideRow2(row Row) bool {
	queryArray := strings.Split(row.query, "")
	return (queryArray[row.index1-1] == row.letter) != (queryArray[row.index2-1] == row.letter)
}

func parse2(input string) ([]Row, error) {
	re := regexp.MustCompile("([0-9]*)-([0-9]*) ([a-z]): ([a-z]*)")
	rowsAsStrings := strings.Split(input, "\n")
	var ret []Row
	for _, rowAsString := range rowsAsStrings {
		found := re.FindStringSubmatch(rowAsString)
		if found != nil {
			index1, err := strconv.Atoi(found[1])
			if err != nil {
				return nil, err
			}
			index2, err := strconv.Atoi(found[2])
			if err != nil {
				return nil, err
			}
			ret = append(ret, Row{
				index1: index1,
				index2: index2,
				letter: found[3],
				query:  found[4],
			})
		}
	}

	return ret, nil
}

func filter(ss []string, test func(string) bool) (ret []string) {
	for _, s := range ss {
		if test(s) {
			ret = append(ret, s)
		}
	}
	return
}
