package main

import (
	"errors"
	"regexp"
	"strconv"
	"strings"
)

type Passport struct {
	byr int
	iyr int
	eyr int
	hgt Height
	hcl Rgb
	ecl Color
	pid string
	cid *string
}

type Rgb struct {
	r string
	g string
	b string
}

type Height struct {
	units Units
	count int
}

type Units string

const (
	inch Units = "in"
	cm         = "cm"
)

type Color string

const (
	amb Color = "amb"
	blu       = "blu"
	brn       = "brn"
	gry       = "gry"
	grn       = "grn"
	hzl       = "hzl"
	oth       = "oth"
)

func parsePassport(row string) (*Passport, error) {
	m := make(map[string]string)
	for _, cell := range strings.FieldsFunc(row, splitByNewLineOrComma) {
		fieldData := strings.Split(cell, ":")
		m[fieldData[0]] = fieldData[1]
	}
	passport := Passport{}

	byr, err := parseNumber(m["byr"])
	if err != nil {
		return nil, err
	}
	passport.byr = byr
	iyr, err := parseNumber(m["iyr"])
	if err != nil {
		return nil, err
	}
	passport.iyr = iyr
	eyr, err := parseNumber(m["eyr"])
	if err != nil {
		return nil, err
	}
	passport.eyr = eyr
	hgt, err := parseHeight(m["hgt"])
	if err != nil {
		return nil, err
	}
	passport.hgt = hgt
	hcl, err := parseRgb(m["hcl"])
	if err != nil {
		return nil, err
	}
	passport.hcl = hcl

	ecl, err := parseColor(m["ecl"])
	if err != nil {
		return nil, err
	}

	pid, exists := m["pid"]
	if !exists {
		return nil, errors.New("pid missing")
	}
	cid, exists := m["cid"]
	var maybeCid *string
	if exists {
		maybeCid = &cid
	} else {
		maybeCid = nil
	}

	return &Passport{
		byr: byr,
		iyr: iyr,
		eyr: eyr,
		hgt: hgt,
		hcl: hcl,
		ecl: ecl,
		pid: pid,
		cid: maybeCid,
	}, nil
}

func parseColor(colorStr string) (Color, error) {
	if contains([]string{"amb", "blu", "brn", "gry", "grn", "hzl", "oth"}, colorStr) {
		return Color(colorStr), nil
	} else {
		return "", errors.New("unknown color")
	}
}

func contains(opts []string, query string) bool {
	for _, item := range opts {
		if query == item {
			return true
		}
	}
	return false
}

func parseRgb(rgbStr string) (Rgb, error) {
	rgbRegex := regexp.MustCompile("^#[0-9a-f]{6}$")
	if rgbRegex.MatchString(rgbStr) {
		return Rgb{
			r: rgbStr[1:3],
			g: rgbStr[3:5],
			b: rgbStr[5:7],
		}, nil
	} else {
		return Rgb{}, errors.New("RGB format mismatch")
	}
}

func parseHeight(h string) (Height, error) {
	for _, units := range []string{"in", "cm"} {
		if strings.HasSuffix(h, units) {
			countStr := strings.TrimSuffix(h, units)
			count, err := strconv.ParseInt(countStr, 10, 32)
			if err != nil {
				return Height{}, err
			}
			return Height{
				units: Units(units),
				count: int(count),
			}, nil
		}
	}
	return Height{}, errors.New("unknown height")
}

func parseNumber(numStr string) (int, error) {
	parsed, err := strconv.ParseInt(numStr, 10, 32)
	if err != nil {
		return 0, err
	}
	num := int(parsed)
	return num, nil
}
