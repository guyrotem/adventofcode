function pointsVisited(instructions) {
    let startPoint = [0, 0];
    let visited = [];
    var currentPoint = startPoint;
    var stepsToGetToCurrent = 0;

    instructions.forEach(instruction => {
        let howMuch = parseInt(instruction.substring(1));
        let where = instruction[0];

        [...Array(howMuch).keys()].map(x => x + 1).forEach(steps => {
            if (where === 'U')
                visited.push([currentPoint[0], currentPoint[1] + steps, stepsToGetToCurrent + steps]);
            else if (where === 'D')
                visited.push([currentPoint[0], currentPoint[1] - steps, stepsToGetToCurrent + steps]);
            else if (where === 'L')
                visited.push([currentPoint[0] - steps, currentPoint[1], stepsToGetToCurrent + steps]);
            else if (where === 'R')
                visited.push([currentPoint[0] + steps, currentPoint[1], stepsToGetToCurrent + steps]);
        });

        currentPoint = visited[visited.length - 1];
        stepsToGetToCurrent += howMuch;
    });

    return visited;
}

function buildMap(arr3) {
    let result = {};

    function add(x, y, v) {
        if (!result[x]) {
            result[x] = {};
        }

        if (!result[x][y]) {
            result[x][y] = v;	//	on duplicates, take 1st
        }
    }

    arr3.forEach(el => add(el[0], el[1], el[2]));

    return {
        get: (x, y) => {
            if (result[x] !== undefined && result[x][y] !== undefined) {
                return result[x][y];
            } else {
                return -1;
            }
        },
        raw: result
    };
}

function intersections(visited1, visited2) {
    let visited2Map = buildMap(visited2);
    console.log(visited2Map);
    console.log(buildMap(visited1));
    let ix = visited1.filter(v1 => visited2Map.get(v1[0], v1[1]) > -1);

    ix.forEach(x => {
        x[2] = [x[2], visited2Map.get(x[0], x[1])];
    });

    return ix;
}

function minIntersection(instructions1, instructions2) {
    let points1 = pointsVisited(instructions1);
    let points2 = pointsVisited(instructions2);
    let ix = intersections(points1, points2);

    console.log(ix);

    let manhattanDistance = ix.reduce((acc, cur) => {
        let curValue = Math.abs(cur[0]) + Math.abs(cur[1]);
        return Math.min(acc, curValue);
    }, Infinity);

    let walkingDistance = ix.reduce((acc, cur) => {
        let curValue = cur[2][0] + cur[2][1];
        return Math.min(acc, curValue);
    }, Infinity);

    return [manhattanDistance, walkingDistance];
}