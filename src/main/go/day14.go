package main

import (
	"fmt"
	"regexp"
	"strconv"
	"strings"
)

func day14() {
	input := strings.Split(MustReadFile("../inputs/2020/14.txt"), "\n")
	var mask string
	mem := make(map[int64]int64)
	memRegex := regexp.MustCompile("mem\\[([0-9]*)\\] = ([0-9]*)")
	for _, line := range input {
		if line[:4] == "mask" {
			mask = line[7:]
		} else {
			match := memRegex.FindStringSubmatch(line)
			address := mustParseInt64(match[1], 10)
			value := mustParseInt64(match[2], 10)

			//mem[address] = masking1(mask, value)
			for _, decodedAddress := range masking2(mask, address) {
				mem[decodedAddress] = value
			}
		}
	}
	sum := int64(0)
	for _, v := range mem {
		sum += v
	}
	fmt.Println(sum)
}

func masking2(mask string, address int64) []int64 {
	bit := 0
	tempRet := []string{""}
	for bit < 36 {
		if mask[35-bit] == '1' {
			for idx := range tempRet {
				tempRet[idx] = "1" + tempRet[idx]
			}
		} else if mask[35-bit] == '0' {
			for idx := range tempRet {
				tempRet[idx] = strconv.Itoa(int((address>>bit)&1)) + tempRet[idx]
			}
		} else {
			var tempRet2 []string

			for idx := range tempRet {
				tempRet2 = append(tempRet2, "0"+tempRet[idx])
			}
			for idx := range tempRet {
				tempRet2 = append(tempRet2, "1"+tempRet[idx])
			}
			tempRet = tempRet2
		}
		bit++
	}
	var ret []int64

	for _, v := range tempRet {
		ret = append(ret, mustParseInt64(v, 2))
	}
	return ret
}

func masking1(mask string, value int64) int64 {
	bit := 0
	ret := ""
	for bit < 36 {
		if mask[35-bit] == '1' {
			ret = "1" + ret
		} else if mask[35-bit] == '0' {
			ret = "0" + ret
		} else {
			if (value>>bit)&1 == 1 {
				ret = "1" + ret
			} else {
				ret = "0" + ret
			}
		}
		bit++
	}
	return mustParseInt64(ret, 2)
}
