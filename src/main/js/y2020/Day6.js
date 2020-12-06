const _ = require('lodash');
const fs = require('fs');

const input = fs.readFileSync('../../inputs/2020/6.txt', 'utf8').split('\n\n');

const sum1 = _.sum(input.map((group) => {
  return _.uniq(
    group
      .split('\n')
      .flatMap(x => x.split(""))
  ).length
}));

const sum2 = _.sum(input.map((group) => {
  return _.intersection(
    ...group
      .split('\n')
      .map(x => x.split(""))
  ).length
}));

console.log(sum1) //  6530
console.log(sum2) //  3323