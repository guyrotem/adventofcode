const fs = require('fs');

const input = fs.readFileSync('../../inputs/2020/4.txt', 'utf8').split('\n\n');

const parsed = input.map(row => {
  const obj = {}
  row
    .split(/[ \n]/)
    .map(cell => {
    const [a, b] = cell.split(':');
    obj[a] = b;
  });
  return obj;
});

function validateNumber(numberAsString, min, max) {
  const num = parseInt(numberAsString);
  return num.toString(10) === numberAsString && num >= min && num <= max;
}

function validateHeight(hgt) {
  if (hgt.endsWith('in')) {
    return validateNumber(hgt.substring(0, hgt.length - 2), 59, 76);
  } else if (hgt.endsWith('cm')) {
    return validateNumber(hgt.substring(0, hgt.length - 2), 150, 193);
  }
}

function validateRgb(hcl) {
  return hcl.match(/^#[0-9a-f]{6}$/g);
}

function validateIn(query, strings) {
  return strings.indexOf(query) > -1;
}

function validatePassport(pid) {
  return pid.match(/^[0-9]{9}$/g);
}

const valid = parsed.filter(item => {
  const expectedFields = ['byr', 'iyr', 'eyr', 'hgt', 'hcl', 'ecl', 'pid'];

  return expectedFields.every(field => Object.keys(item).indexOf(field) > -1) &&
    validateNumber(item.byr, 1920, 2002)
    && validateNumber(item.iyr, 2010, 2020)
    && validateNumber(item.eyr, 2020, 2030)
    && validateHeight(item.hgt)
    && validateRgb(item.hcl)
    && validateIn(item.ecl, ['amb', 'blu', 'brn', 'gry', 'grn', 'hzl', 'oth'])
    && validatePassport(item.pid);
});

console.log(parsed.length); //  259
console.log(valid.length);  //  101