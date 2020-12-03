package main

import (
	"io/ioutil"
	"path/filepath"
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
