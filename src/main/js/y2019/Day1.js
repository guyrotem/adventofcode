function parseInput(txt) { return txt.split('\n').map(x => parseInt(x)) }
function fuel(mass) { return Math.floor(mass / 3) - 2; }
function fuelRec(mass) {
    const res = Math.floor(mass / 3) - 2;
    return res <= 0 ? 0 : res + fuelRec(res);
}
