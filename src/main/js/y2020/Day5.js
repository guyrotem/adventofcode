const _ = require('lodash');
const fs = require('fs');

const input = fs.readFileSync('../../inputs/2020/5.txt', 'utf8').split('\n');

const seatIds = input.map(row => {
  const line = parseInt(row.substring(0, 7).split('F').join('0').split('B').join('1'), 2)
  const seat = parseInt(row.substring(7).split('L').join('0').split('R').join('1'), 2)
  return 8 * line + seat;
})

const sorted = _.sortBy(seatIds);
const continuityBreak = sorted.find((v, idx) => sorted[idx + 1] !== v + 1);

console.log(sorted.reverse()[0]); //  813
console.log(continuityBreak + 1); //  612
