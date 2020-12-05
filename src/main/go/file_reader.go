package main

import (
	"io/ioutil"
	"path/filepath"
	"strconv"
)

func ReadFile(fileName string) (*string, error) {
	path, err := filepath.Abs(fileName)
	if err != nil {
		return nil, err
	}
	content, err := ioutil.ReadFile(path)

	if err != nil {
		return nil, err
	}
	stringContent := string(content)
	return &stringContent, nil
}

func MustReadFile(filename string) string {
	content, err := ReadFile(filename)
	if err != nil {
		panic(`Failed to read file: ` + strconv.Quote(filename) + ` | ` + err.Error())
	}
	return *content
}
