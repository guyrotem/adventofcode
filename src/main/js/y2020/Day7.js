const _ = require('lodash');
const fs = require('fs');

const input = fs.readFileSync('../../inputs/2020/7.txt', 'utf8').split('\n');

const data = {};
const bagRegex = /^([0-9]*) ([a-z][a-z ]*) bag[\\s]?$/

function parse(line) {
  const [key, unparsedVal] = line.split(' bags contain ');
  const unparsedBags = unparsedVal.substring(0, unparsedVal.length - 1).split(', ');

  data[key] = unparsedBags
    .filter(x => x !== 'no other bags')
    .map(unparsedBag => {
      const [numStr, color] = unparsedBag.match(bagRegex).slice(1);
      return {
        color,
        count: parseInt(numStr),
      };
    });
}

input.forEach(parse);

console.log(data);

const initial = ['shiny gold'];

function findBagsThatContain(lookup) {
  return Object.keys(data).filter(color => {
    return _.some(
      data[color],
      bag => lookup.indexOf(bag.color) > -1,
    );
  });
}

let ptr = initial;
let prevLen;

do {
  prevLen = ptr.length;
  ptr = _.uniq(findBagsThatContain(ptr).concat(ptr))
} while (ptr.length > prevLen)

console.log(ptr.length - 1); //  259

function rank(color) {
  return 1 + _.sum(data[color].map(bag => bag.count * rank(bag.color)));
}

console.log(rank(initial[0]) - 1);  //  45018