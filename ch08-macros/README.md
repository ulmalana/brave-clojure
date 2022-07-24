# Chapter 08 - Macros

## Macros vs. Functions

Macro definitions is similar to function definition. One key difference between macros and functions:
1. Function arguments are **fully evaluated** before passing them to the function.
2. Macros arguments are **unevaluated data**.

```
(defmacro infix-2
    [[operand1 op operand2]]
    (list op operand1 operand2))

;; (3 + 4) is unevaluated data
(macroexpand '(infix-2 (3 + 4)))
; => (+ 3 4)
```
