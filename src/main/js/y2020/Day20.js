const fs = require('fs');

const input = fs.readFileSync('../../inputs/2020/20.txt', 'utf8').split('\n\n');
const tileSize = 10;
const upMap = new Map();
const downMap = new Map();
const leftMap = new Map();
const rightMap = new Map();

const tiles = input.map(parseTile);
const rotations = tiles.flatMap(createRotations);

rotations.forEach((rotation) => {
  upMap.set(rotation.up, (upMap.get(rotation.up) || []).concat(rotation));
  downMap.set(rotation.down, (downMap.get(rotation.down) || []).concat(rotation));
  leftMap.set(rotation.left, (leftMap.get(rotation.left) || []).concat(rotation));
  rightMap.set(rotation.right, (rightMap.get(rotation.right) || []).concat(rotation));
});

function createRotations(tile) {
  const flipped = flipHorizontal(tile);

  return [
    tile,
    rotateCw(tile),
    rotateCw(rotateCw(tile)),
    rotateCw(rotateCw(rotateCw(tile))),
    flipped,
    rotateCw(flipped),
    rotateCw(rotateCw(flipped)),
    rotateCw(rotateCw(rotateCw(flipped))),
  ];
}

function rotateImage(tile) {
  const newTile = [];
  for (let i = 0; i < tile[0].length; i++) {
    newTile.push(tile.map(row => row[i]).reverse());
  }
  return newTile;
}

function rotateCw(tile) {
  const newTile = rotateImage(tile.tile);

  return {
    id: tile.id,
    tile: newTile,
    up: tile.left,
    right: tile.up,
    down: tile.right,
    left: tile.down,
  }
}

function reverseBin(num) {
  const binStr = num.toString(2);
  const binStrPadded = Array(tileSize - binStr.length).fill('0').join('') + binStr;
  return parseInt(reverse(binStrPadded), 2);
}

function reverse(str) {
  return str.split('').reverse().join('');
}

function flipHorizontal(tile) {
  const newTile = tile.tile.map(row => reverse(row));
  return {
    id: tile.id,
    tile: newTile,
    right: reverseBin(tile.left),
    left: reverseBin(tile.right),
    down: reverseBin(tile.down),
    up: reverseBin(tile.up),
  }
}

function getCode(line) {
  return parseInt(
    line
      .replace(/\./g, '0')
      .replace(/#/g, '1'),
    2
  )
}

function parseTile(tileRaw) {
  const id = parseInt(tileRaw.substring(5, 9));
  const tile = tileRaw.split('\n').splice(1);

  const up = getCode(tile[0]);
  const right = getCode(tile.map(row => row[row.length - 1]).join(''));
  const down = getCode(reverse(tile[tile.length - 1]));
  const left = getCode(reverse(tile.map(row => row[0]).join('')));

  return {
    id,
    tile,
    up,
    down,
    left,
    right,
  };
}

function rightNeighbors(tile) {
  return leftMap.get(reverseBin(tile.right))
    .filter(other => other.id !== tile.id);
}

function downNeighbors(tile) {
  return upMap.get(reverseBin(tile.down))
    .filter(other => other.id !== tile.id);
}

function findEmptyCell(board, boardSize) {
  for (let row = 0; row < boardSize; row++) {
    for (let col = 0; col < boardSize; col++) {
      if (!board[row][col]) {
        return {
          row,
          col,
        };
      }
    }
  }
  return null;
}

function candidateOnBoard(board, candidate) {
  return board
    .flatMap(x => x.filter(t => t !== null))
    .map(x => x.id)
    .indexOf(candidate.id) > -1;
}

let globalSolved;

function resolve(board, boardSize) {
  const emptyCell = findEmptyCell(board, boardSize);
  if (!emptyCell) {
    globalSolved = [];
    let rowIdx = 0;
    board.forEach(row => {
      globalSolved.push([]);
      row.forEach(cell => globalSolved[rowIdx].push(cell));
      rowIdx++;
    });

    return true;
  }
  const { row, col } = emptyCell;

  let candidates;
  if (row === 0) {
    candidates = rightNeighbors(board[row][col - 1]);
  } else if (col === 0) {
    candidates = downNeighbors(board[row - 1][col]);
  } else {
    const down = downNeighbors(board[row - 1][col]);
    const right = rightNeighbors(board[row][col - 1]);
    candidates = down.filter(t => right.findIndex(oT => oT.id === t.id) > -1);
  }
  let finishedBoard;
  return candidates.find((candidate) => {
    if (!candidateOnBoard(board, candidate)) {
      board[row][col] = candidate;
      finishedBoard = resolve(board, boardSize);
      board[row][col] = null;
      return finishedBoard;
    } else return false;
  })
}

const boardSize = Math.sqrt(tiles.length);

tiles.some((tile) => {
  const tmp = Array(boardSize).fill(null);
  const emptyBoard = [];
  tmp.forEach(() => emptyBoard.push(tmp.slice()));

  emptyBoard[0][0] = tile;
  return resolve(emptyBoard, boardSize);
});

const board = globalSolved;
//13224049461431
console.log(board[0][0].id * board[0][boardSize - 1].id * board[boardSize - 1][0].id * board[boardSize - 1][boardSize - 1].id);

function removeEdges(board) {
  return board
      .map(row => row.map(tile => stripEdges(tile.tile)))
      .flatMap(row => {
        return row.reduce((acc, cell) => {
          while (acc.length < cell.length) acc.push([]);
          cell.forEach((cellRow, idx) => {
            acc[idx].push(...cellRow);
          })
          return acc;
        }, [])
      });
}

function stripEdges(tile) {
  return tile.slice(1, tile.length - 1).map(row => row.slice(1, tile.length - 1));
}

let image = removeEdges(globalSolved);

const monster = `                  # 
#    ##    ##    ###
 #  #  #  #  #  #   `.split('\n');

const monsterCoords = [];
monster.forEach((row, rowIdx) => {
  row.split('').forEach((cell, colIdx) => {
    if (cell === '#') {
      monsterCoords.push({rowIdx, colIdx});
    }
  });
});

function countMonsters(image, coords) {
  let monsters = 0;
  for (let x = 0; x < image.length - 2; x++) {
    for (let y = 0; y < image[0].length - 19; y++) {
      if (coords.every(coord => image[x + coord.rowIdx][y + coord.colIdx] === '#')) {
        monsters++;
      }
    }
  }
  return monsters;
}

let monstersCount = 0
monstersCount += countMonsters(image, monsterCoords);
monstersCount += countMonsters(rotateImage(image), monsterCoords);
monstersCount += countMonsters(rotateImage(rotateImage(image)), monsterCoords);
monstersCount += countMonsters(rotateImage(rotateImage(rotateImage(image))), monsterCoords);

image = image.map(row => row.reverse());

monstersCount += countMonsters(image, monsterCoords);
monstersCount += countMonsters(rotateImage(image), monsterCoords);
monstersCount += countMonsters(rotateImage(rotateImage(image)), monsterCoords);
monstersCount += countMonsters(rotateImage(rotateImage(rotateImage(image))), monsterCoords);

const totalHashes = image.flatMap(x => x).filter(x => x === '#').length;

console.log(totalHashes - monstersCount * monsterCoords.length);  //  2231