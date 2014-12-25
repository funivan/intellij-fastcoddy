All variables MUST be in the format  `$VAR_<your_variable_name>$`  for example `$VAR_METHOD_NAME$` or `$VAR_USER$`

## 1.1 Simple Variables example

Configuration:
```js
  {
    items: [
      {
        shortcut: 'len',
        expand: 'if(empty($VAR_STRING$) or strlen($VAR_STRING$)<$VAR_LEN$){\n return false;\n}'
      }
    ]
  }
```


## 1.2 How to use variables expression

Configuration:
```js
{
  items: [
    {
      shortcut: '_s',
      expand: 'public function set$VAR_METHOD$($$VAR_NAME$){\n $this->$VAR_NAME$ = $$VAR_NAME$; \n return $this;\n}',
      vars : {
         VAR_NAME : {
            expression : "decapitalize(VAR_METHOD)"
         }
      }
    }
  ]
}
```

For example we type `_s` and expand it

You code will be
```php
    public function set<CURSOR>($){
      $this-> = $;
      return $this;
    }
```

Cursor automatically goes to first variable (`$VAR_METHOD$`)
You Type `UserName` and hit enter

At this point you have

```php
     public function setUserName($userName){
       $this->$userName = $userName;
       return $this;
     }
```

## 1.3 How to use default value

Configuration:
```js
{
  items: [
    {
      shortcut: 'log',
      expand: '$this->error("Error. Line:$VAR_LINE$");',
      vars: {
        VAR_Line:{
          defaultValue: "lineNumber()"
        }
      }
    }
  ]
}
```

Type `log` in line `14` and you will get
```php
  $this->error("Error. Line:14");
```
Not that a `defaultValue` is an expression that can refer to other live template variables.


## 1.4 How to use `alwaysStopAt` value directive

Configuration:
```js
{
  items: [
    {
      shortcut: 'log',
      expand: '$this->error("Error. Line:$VAR_LINE$");',
      vars: {
        VAR_Line:{
          defaultValue: "lineNumber()",
          alwaysStopAt : true
        }
      }
    }
  ]
}
```
`alwaysStopAt` used to rewrite value.
For example in some cases you want to change default value. Use `alwaysStopAt:true`

Type `log` in line `14` and you will get
```php
  $this->error("Error. Line:<CURSOR>14");
```

At this point you can hit enter and get result or change `line number` to other. For example `13`


### Links
Variables expression for PHPStorm
http://www.jetbrains.com/phpstorm/webhelp/edit-template-variables-dialog.html#functions


