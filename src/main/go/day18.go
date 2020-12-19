package main

import (
	"fmt"
	"regexp"
	"strconv"
	"strings"
)

func day18() {
	input := strings.Split(MustReadFile("../inputs/2020/18.txt"), "\n")

	fmt.Println(calcLeftAssociative("5 + (8 * 3 + 9 + 3 * 4 * 3)") == 437)
	fmt.Println(calcLeftAssociative("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2") == 13632)

	sum1 := int64(0)
	sum2 := int64(0)
	for _, line := range input {
		line2 := addPrecedenceParentheses(line, "+")
		sum1 += calcLeftAssociative(line)
		sum2 += calcLeftAssociative(line2)
	}
	fmt.Println(sum1) //510009915468
	fmt.Println(sum2) //321176691637769
}

func addPrecedenceParentheses(input string, op string) string {
	pluses := regexp.MustCompile("\\"+op).FindAllStringSubmatchIndex(input, -1)
	ret := input
	for idx := range pluses {
		plus := regexp.MustCompile("\\"+op).FindAllStringSubmatchIndex(ret, -1)[idx][0]
		lhStart, rhStop := findExpressionsAround(ret, plus)
		ret = ret[:lhStart] + "(" + ret[lhStart:rhStop] + ")" + ret[rhStop:]
	}
	return ret
}

func findExpressionsAround(input string, idx int) (int, int) {
	a := findExpressionEndIndex(input[:idx-1], false)
	b := findExpressionEndIndex(input[idx+2:], true)
	return idx - 1 - a, idx + 2 + b
}

func Reverse(s string) (result string) {
	for _, v := range s {
		result = string(v) + result
	}
	return
}

func findExpressionEndIndex(input string, ltr bool) int {
	if !ltr {
		input = Reverse(input)
	}
	numStartMatch := regexp.MustCompile("^([0-9]+)").FindStringSubmatch(input)
	if len(numStartMatch) > 0 {
		return len(numStartMatch[1])
	} else if (ltr && input[0] == '(') || (!ltr && input[0] == ')') {
		return findEnclosingP(input, ltr)
	} else {
		panic(input)
	}
}

func calcLeftAssociative(input string) int64 {
	numberLhOpRegex := regexp.MustCompile("^([0-9]*) ([+*]) (.*)$")
	numberRegex := regexp.MustCompile("^([0-9]*)$")

	numberLhOpFound := numberLhOpRegex.FindStringSubmatch(input)
	numberFound := numberRegex.FindStringSubmatch(input)

	if len(numberFound) > 0 {
		return mustParseInt64(input, 10)
	} else if len(numberLhOpFound) == 4 {
		lh := mustParseInt64(numberLhOpFound[1], 10)
		operand := numberLhOpFound[2]
		head, tail := headTail(numberLhOpFound[3])
		rh := calcLeftAssociative(head)

		var calcResult int64
		if operand == "+" {
			calcResult = lh + rh
		} else if operand == "*" {
			calcResult = lh * rh
		} else {
			panic(operand)
		}

		if tail == "" {
			return calcResult
		} else {
			return calcLeftAssociative(strconv.FormatInt(calcResult, 10) + tail)
		}
	} else if input[0] == '(' {
		head, tail := headTail(input)

		headEval := calcLeftAssociative(head)
		if tail == "" {
			return headEval
		} else {
			return calcLeftAssociative(strconv.FormatInt(headEval, 10) + tail)
		}
	} else {
		panic(`[` + input + `]`)
	}
}

func headTail(input string) (string, string) {
	numberLhOpRegex := regexp.MustCompile("^([0-9]*) ([+*]) (.*)$")
	numberRegex := regexp.MustCompile("^([0-9]*)$")

	numberOp := numberLhOpRegex.FindStringSubmatch(input)

	if len(numberRegex.FindStringSubmatch(input)) > 0 {
		return input, ""
	} else if len(numberOp) == 4 {
		return numberOp[1], " " + numberOp[2] + " " + numberOp[3]
	} else if input[0] == '(' {
		parensEnd := findEnclosingP(input, true)
		if parensEnd < len(input)-1 {
			return input[1:parensEnd], input[parensEnd+1:]
		} else {
			return input[1:parensEnd], ""
		}
	} else {
		panic(input)
	}
}

func findEnclosingP(input string, ltr bool) int {
	var start int32
	var stop int32

	if ltr {
		start = '('
		stop = ')'
	} else {
		start = '('
		stop = ')'
	}
	depth := 0
	for idx, v := range input {
		if v == start {
			depth++
		} else if v == stop {
			depth--
		}

		if depth == 0 {
			return idx
		}
	}
	panic(input)

}
