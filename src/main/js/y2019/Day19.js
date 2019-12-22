const code = [109,424,203,1,21101,0,11,0,1106,0,282,21102,1,18,0,1106,0,259,2101,0,1,221,203,1,21101,0,31,0,1106,0,282,21101,0,38,0,1106,0,259,21001,23,0,2,21202,1,1,3,21102,1,1,1,21102,1,57,0,1106,0,303,2102,1,1,222,20102,1,221,3,21001,221,0,2,21102,1,259,1,21102,80,1,0,1106,0,225,21102,106,1,2,21102,91,1,0,1105,1,303,1201,1,0,223,21001,222,0,4,21101,259,0,3,21102,1,225,2,21101,225,0,1,21101,0,118,0,1106,0,225,20101,0,222,3,21102,42,1,2,21101,133,0,0,1105,1,303,21202,1,-1,1,22001,223,1,1,21101,0,148,0,1106,0,259,1201,1,0,223,21001,221,0,4,20101,0,222,3,21101,10,0,2,1001,132,-2,224,1002,224,2,224,1001,224,3,224,1002,132,-1,132,1,224,132,224,21001,224,1,1,21101,195,0,0,106,0,108,20207,1,223,2,20102,1,23,1,21101,-1,0,3,21101,214,0,0,1105,1,303,22101,1,1,1,204,1,99,0,0,0,0,109,5,1202,-4,1,249,22102,1,-3,1,22101,0,-2,2,21202,-1,1,3,21101,250,0,0,1105,1,225,21202,1,1,-4,109,-5,2106,0,0,109,3,22107,0,-2,-1,21202,-1,2,-1,21201,-1,-1,-1,22202,-1,-2,-2,109,-3,2105,1,0,109,3,21207,-2,0,-1,1206,-1,294,104,0,99,22102,1,-2,-2,109,-3,2106,0,0,109,5,22207,-3,-4,-1,1206,-1,346,22201,-4,-3,-4,21202,-3,-1,-1,22201,-4,-1,2,21202,2,-1,-1,22201,-4,-1,1,21202,-2,1,3,21101,343,0,0,1106,0,303,1105,1,415,22207,-2,-3,-1,1206,-1,387,22201,-3,-2,-3,21202,-2,-1,-1,22201,-3,-1,3,21202,3,-1,-1,22201,-3,-1,2,22101,0,-4,1,21102,384,1,0,1106,0,303,1105,1,415,21202,-4,-1,-4,22201,-4,-3,-4,22202,-3,-2,-2,22202,-2,-4,-4,22202,-3,-2,-3,21202,-4,-1,-2,22201,-3,-2,1,22102,1,1,-4,109,-5,2105,1,0];

let counter = 0;
function sampleAt(i, j) {
    return new IntCodeMachine(code).runProgram([i, j]).output[0];
}

for (let i = 0; i < 50; i++) {
    for (let j = 0; j < 50; j++) {
        if (sampleAt(i, j) === 1) counter++;
    }
}

console.log(counter);   //116

function draw(xStart, yStart, blockSize = 100) {
    const data = [];
    for (let j = yStart; j < yStart + blockSize; j++) {
        let row = '';
        for (let i = xStart; i < xStart + blockSize; i++) {
            row += sampleAt(i, j);
        }
        data.push(row);
    }

    data.forEach(x => console.log(x));
}

draw(20, 30, 35);

function frame(topLeftCorner, size = 100) {
    return {
        topRight: sampleAt(topLeftCorner.x + size - 1, topLeftCorner.y),
        bottomLeft: sampleAt(topLeftCorner.x, topLeftCorner.y + size - 1)
    };
}

function correctFrame(topLeftCorner, size = 100) {
    return radiated(frame(topLeftCorner, size)) &&
        !radiated(frame(shift(topLeftCorner, 0, -1), size)) &&
        !radiated(frame(shift(topLeftCorner, -1, 0), size)) &&
        !radiated(frame(shift(topLeftCorner, -1, -1), size))
}

function shift(xy, deltaX, deltaY) {
    return {
        x: xy.x + deltaX,
        y: xy.y + deltaY
    };
}

function radiated(result) {
    return result.topRight === 1 && result.bottomLeft === 1;
}

function findCorner(size) {
    let search = {x: 0, y: 0};

    while (!correctFrame(search, size)) {
        let res = frame(search, size);
        search = shift(search, 1 - res.bottomLeft, 1 - res.topRight);
    }

    return search;
}

console.log(findCorner(10));
console.log(findCorner(100));   //  1031, 1666