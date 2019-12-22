
function walk(from, direction) {
    if (direction === 0) {
        return [from[0], from[1] + 1];
    } else if (direction === 1) {
        return [from[0] + 1, from[1]];
    } else if (direction === 2) {
        return [from[0], from[1] - 1];
    } else {
        return [from[0] - 1, from[1]];
    }
}

function turn(direction, input) {
    if (input === 0) {
        return (direction + 3) % 4;
    } else {
        return (direction + 1) % 4;
    }
}

function startPainting(whiteBlocks = []) {

    const robotPainterCode = new IntCodeMachine(
        [3, 8, 1005, 8, 339, 1106, 0, 11, 0, 0, 0, 104, 1, 104, 0, 3, 8, 1002, 8, -1, 10, 101, 1, 10, 10, 4, 10, 1008, 8, 0, 10, 4, 10, 1002, 8, 1, 29, 2, 1108, 11, 10, 1, 1, 20, 10, 2, 107, 6, 10, 3, 8, 102, -1, 8, 10, 101, 1, 10, 10, 4, 10, 108, 0, 8, 10, 4, 10, 101, 0, 8, 62, 1006, 0, 29, 1006, 0, 12, 1, 1101, 5, 10, 1, 2, 20, 10, 3, 8, 102, -1, 8, 10, 1001, 10, 1, 10, 4, 10, 1008, 8, 0, 10, 4, 10, 1001, 8, 0, 99, 1006, 0, 30, 3, 8, 1002, 8, -1, 10, 1001, 10, 1, 10, 4, 10, 1008, 8, 0, 10, 4, 10, 1001, 8, 0, 124, 1006, 0, 60, 3, 8, 1002, 8, -1, 10, 1001, 10, 1, 10, 4, 10, 1008, 8, 1, 10, 4, 10, 101, 0, 8, 149, 2, 1007, 2, 10, 1, 1105, 10, 10, 3, 8, 1002, 8, -1, 10, 101, 1, 10, 10, 4, 10, 108, 0, 8, 10, 4, 10, 101, 0, 8, 178, 1, 1108, 15, 10, 1, 1101, 5, 10, 1, 109, 8, 10, 1006, 0, 20, 3, 8, 102, -1, 8, 10, 1001, 10, 1, 10, 4, 10, 108, 1, 8, 10, 4, 10, 101, 0, 8, 215, 1006, 0, 61, 1006, 0, 16, 2, 1105, 15, 10, 1006, 0, 50, 3, 8, 1002, 8, -1, 10, 1001, 10, 1, 10, 4, 10, 108, 1, 8, 10, 4, 10, 101, 0, 8, 250, 1, 1003, 10, 10, 1, 9, 19, 10, 2, 1004, 6, 10, 2, 1106, 2, 10, 3, 8, 1002, 8, -1, 10, 1001, 10, 1, 10, 4, 10, 1008, 8, 1, 10, 4, 10, 101, 0, 8, 289, 1, 1103, 13, 10, 2, 105, 17, 10, 3, 8, 1002, 8, -1, 10, 1001, 10, 1, 10, 4, 10, 108, 1, 8, 10, 4, 10, 1002, 8, 1, 318, 101, 1, 9, 9, 1007, 9, 1086, 10, 1005, 10, 15, 99, 109, 661, 104, 0, 104, 1, 21101, 0, 825599304340, 1, 21101, 356, 0, 0, 1106, 0, 460, 21101, 0, 937108545948, 1, 21102, 1, 367, 0, 1106, 0, 460, 3, 10, 104, 0, 104, 1, 3, 10, 104, 0, 104, 0, 3, 10, 104, 0, 104, 1, 3, 10, 104, 0, 104, 1, 3, 10, 104, 0, 104, 0, 3, 10, 104, 0, 104, 1, 21102, 1, 21628980315, 1, 21101, 0, 414, 0, 1105, 1, 460, 21101, 0, 3316673539, 1, 21101, 425, 0, 0, 1106, 0, 460, 3, 10, 104, 0, 104, 0, 3, 10, 104, 0, 104, 0, 21102, 988753428840, 1, 1, 21102, 1, 448, 0, 1106, 0, 460, 21102, 825544569700, 1, 1, 21102, 459, 1, 0, 1106, 0, 460, 99, 109, 2, 21202, -1, 1, 1, 21102, 1, 40, 2, 21102, 491, 1, 3, 21102, 481, 1, 0, 1105, 1, 524, 109, -2, 2106, 0, 0, 0, 1, 0, 0, 1, 109, 2, 3, 10, 204, -1, 1001, 486, 487, 502, 4, 0, 1001, 486, 1, 486, 108, 4, 486, 10, 1006, 10, 518, 1101, 0, 0, 486, 109, -2, 2105, 1, 0, 0, 109, 4, 2102, 1, -1, 523, 1207, -3, 0, 10, 1006, 10, 541, 21102, 0, 1, -3, 21201, -3, 0, 1, 22102, 1, -2, 2, 21102, 1, 1, 3, 21102, 560, 1, 0, 1106, 0, 565, 109, -4, 2105, 1, 0, 109, 5, 1207, -3, 1, 10, 1006, 10, 588, 2207, -4, -2, 10, 1006, 10, 588, 22101, 0, -4, -4, 1105, 1, 656, 21202, -4, 1, 1, 21201, -3, -1, 2, 21202, -2, 2, 3, 21102, 1, 607, 0, 1106, 0, 565, 22102, 1, 1, -4, 21101, 0, 1, -1, 2207, -4, -2, 10, 1006, 10, 626, 21101, 0, 0, -1, 22202, -2, -1, -2, 2107, 0, -3, 10, 1006, 10, 648, 21202, -1, 1, 1, 21101, 0, 648, 0, 105, 1, 523, 21202, -2, -1, -2, 22201, -4, -2, -4, 109, -5, 2105, 1, 0]
    );

    const BLACK = 0;
    const WHITE = 1;

    let robotLocation = [0, 0];
    let direction = 0;
    const paintedWhite = {};

    whiteBlocks.forEach(block => paintedWhite[block] = true);

    let paintCounter = 0;

    do {
        let nextInput = paintedWhite[robotLocation] === true ? WHITE : BLACK;
        let runResult = robotPainterCode.runUntilOutputOrHalt([nextInput]);
        if (runResult.halt) {
            break;
        }
        let color = runResult.output[0];
        let turnInput = robotPainterCode.runUntilOutputOrHalt([]).output[0];

        paintedWhite[robotLocation] = color === WHITE;

        direction = turn(direction, turnInput);
        robotLocation = walk(robotLocation, direction);

        paintCounter++;
    } while (true);

    return paintedWhite;
}

const modifiedBlocks = Object.keys(startPainting([]));
console.log(modifiedBlocks.length);

const part2output = startPainting([[0, 0]]);

Object.filter = (obj, predicate) =>
    Object.keys(obj)
        .filter( key => predicate(obj[key]) )
        .reduce( (res, key) => (res[key] = obj[key], res), {} );

//  input: {[x1,y1]: true, [x2,y2]: false, ...}
function paintMap(map) {
    const onlyTrue = Object.filter(map, x => x);
    const paintItems = Object.keys(onlyTrue).map(x => x.split(',').map(x => parseInt(x)));

    const maxX = Math.max.apply(null, paintItems.map(x => x[0]));
    const minX = Math.min.apply(null, paintItems.map(x => x[0]));

    const maxY = Math.max.apply(null, paintItems.map(x => x[1]));
    const minY = Math.min.apply(null, paintItems.map(x => x[1]));

    const canvas = [];

    for (let y = minY; y <= maxY; y++) {
        let print = '';

        for (let x = minX; x <= maxX; x++) {

            if (paintItems.find(a => a[0] === x && a[1] === y)) {
                print += '□'
            } else {
                print += ' '
            }

        }

        canvas.push(print);
    }

    canvas.reverse().forEach(row => console.log(row))
}

paintMap(part2output);