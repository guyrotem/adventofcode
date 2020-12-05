package main

import (
	"fmt"
	"regexp"
	"strings"
)

func day4() {
	input := MustReadFile("../inputs/2020/4.txt")

	rows := strings.Split(input, "\n\n")

	var passports []Passport

	for _, row := range rows {
		passport, _ := parsePassport(row)
		if passport != nil {
			passports = append(passports, *passport)
		}
	}

	valid := 0

	for _, passport := range passports {
		if validatePassport(passport) {
			valid++
		}
	}

	fmt.Println(len(passports))
	fmt.Println(valid)
}

func splitByNewLineOrComma(r rune) bool {
	return r == '\n' || r == ' '
}

func validateHeight(height Height) bool {
	result := false
	if height.units == inch {
		result = numberInRange(height.count, 59, 76)
	} else if height.units == cm {
		result = numberInRange(height.count, 150, 193)
	}
	return result
}

func validatePassport(passport Passport) bool {
	return numberInRange(passport.byr, 1920, 2002) &&
		numberInRange(passport.iyr, 2010, 2020) &&
		numberInRange(passport.eyr, 2020, 2030) &&
		validateHeight(passport.hgt) &&
		validatePassportId(passport.pid)
}

func validatePassportId(pid string) bool {
	return regexp.MustCompile("^[0-9]{9}$").MatchString(pid)
}

func numberInRange(num int, min int, max int) bool {
	return num >= min && num <= max
}
