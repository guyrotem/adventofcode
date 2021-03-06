const fs = require('fs');

const input = fs.readFileSync('../../inputs/2020/3.txt', 'utf8').split('\n');

function calc(s,t) {
  let a = 0;
  for (let i = 0; s*i < input.length; i++) {
    a += input[s*i][(t*i % 31)] === '#' ? 1 : 0;
  }
  return a;
}
console.log(calc(1,3))  //  176

const mult = [
  calc(1,1),
  calc(1,3),
  calc(1,5),
  calc(1,7),
  calc(2, 1)
].reduce((x, y) => x * y);
console.log(mult);  //5872458240
