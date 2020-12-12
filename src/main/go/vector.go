package main

type Vector struct {
	x int
	y int
}

func NewVector(x int, y int) *Vector {
	return &Vector{
		x,
		y,
	}
}

func (vector *Vector) Rotate(degrees int) *Vector {
	newVector := *vector
	newVector.RotateInPlace(degrees)
	return &newVector
}

func (vector *Vector) RotateInPlace(degrees int) {
	x := vector.x
	y := vector.y
	switch degrees % 360 {
	case 0:
		return
	case 90:
		vector.x = -1 * y
		vector.y = x
	case 180:
		vector.x = -1 * x
		vector.y = -1 * y
	case 270:
		vector.x = y
		vector.y = -1 * x
	default:
		panic(degrees)
	}
}

func (vector *Vector) MultiplyInPlace(factor int) {
	vector.x = vector.x * factor
	vector.y = vector.y * factor
}

func (vector *Vector) Multiply(factor int) *Vector {
	newVector := *vector
	newVector.MultiplyInPlace(factor)
	return &newVector
}

func (vector *Vector) AddInPlace(other *Vector) {
	vector.x = vector.x + other.x
	vector.y = vector.y + other.y
}

func (vector *Vector) Add(other *Vector) *Vector {
	newVector := *vector
	newVector.AddInPlace(other)
	return &newVector
}
