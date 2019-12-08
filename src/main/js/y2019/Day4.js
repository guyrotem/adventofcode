function checkNumber(input) {
    let inputString = input.toString();
    return [0,1,2,3,4].map(i => inputString[i] <= inputString[i+1]).indexOf(false) === -1 &&
        [0,1,2,3,4].map(i => inputString[i] === inputString[i+1]).indexOf(true) > -1
}

function checkNumber2(input) {
    let inputString = input.toString();
    return [0,1,2,3,4].map(i => inputString[i] <= inputString[i+1]).indexOf(false) === -1 &&
        [0,1,2,3,4].map(i => inputString[i] === inputString[i+1] && ((i === 0 || inputString[i-1] !== inputString[i]) && (i === 4 || inputString[i+1] !== inputString[i+2]))).indexOf(true) > -1
}