All variables MUST be in the format  `$VAR_<your_variable_name>$`  for example `$VAR_METHOD_NAME$` or `$VAR_USER$`

## 1.1 Simple Variables example
Configuration:
```js
  {
    items: [
      {
        group: 'if',
        shortcut: '(eli|if)',
        expand: ' $1($TAB1$){\n$TAB2$}',
        isRegex: true,
        regexpReplaces: {
          'eli': 'elseif'
        },
        tabs: {
          END: [ 'if' ],
          TAB1: [  'variable_handling', 'logical_operators' ],
          TAB2: [ 'control_structure' ]
        }
      },
      {
        group: 'logical_operators',
        shortcut: '(\\&|\\|)',
        isRegex: true,
        expand: ' $1 $TAB1$',
        regexpReplaces: {
          '&': 'and',
          '|': 'and',
        },
        tabs: {
          TAB1: [ 'variable_handling' ]
        }
      },
      {
        group: 'variable_handling',
        shortcut: '(\!|)(iso|isf|isd|isa|isi|isn|isc|in|i|e)',
        expand: ' $1$2($TAB1$) $TAB2$',
        isRegex: true,
        tabs: {
          TAB2: [ 'valueCheck' ]
        },
        regexpReplaces: {
          'iso': 'is_object',
          'isf': 'is_file',
          'isd': 'is_dir',
          'isa': 'is_array',
          'isc': 'is_callable',
          'isi': 'is_integer',
          'isn': 'is_numeric',
          'in' : 'in_array',
          'i'  : 'isset',
          'e'  : 'empty'
        }
      },
      {
        group: 'control_structure',
        shortcut: '(co|br|rf|rt|rn|rs|r\\$)',
        expand: '$1; $TAB3$',
        isRegex: true,
        regexpReplaces: {
          'co': 'continue',
          'br': 'break',
          'r$': 'return \\$this',
          'rs': 'return ""',
          'rf': 'return false',
          'rt': 'return true',
          'rn': 'return null'
        }
      }
    ]
  }
```
