" Vim syntax file
" Language:     SMX (SiMpleX or Simple Markup for XML)
" Maintainer:   Robert Iannucci Jr. <riannucc@calpoly.edu>
" Last Change:  13 March 2007 18:21:00 PST
" Filenames:    *.smx

" CONFIGURATION:
"   syntax folding can be turned on by
"
"       let g:smx_syntax_folding = 1
"
"   before the syntax file gets loaded (e.g. in ~/.vimrc).
"   This might slow down syntax highlighting significantly,
"   especially for large files.
"
"   REFERENCES:
"       [1] http://www.csc.calpoly.edu/~riannucc/smx

" Quit when a syntax file was already loaded
if exists("b:current_syntax")
    finish
endif

let s:smx_cpo_save = &cpo
set cpo&vim

syn case match
    
syn match strEsc    +\\" | \\'+
syn match multiTag  +&&+ contained
syn match starTag   +\*>+ contained
syn match starTag   +\*\*>+ contained
syn match PITag     +?>+ contained

" strings, aka values
" tag foo.attrib = "value" >
"                  ^^^^^^^
"
" PROVIDES: @Spell
syn region smxString matchgroup=Quote start=+'+ end=+'+ skip=+\\'+ contains=strEsc,@Spell display
syn region smxString matchgroup=Quote start=+"+ end=+"+ skip=+\\"+ contains=strEsc,@Spell display


" punctuation (within attributes) e.g. tag smx:foo.attribute ... >
"                                             ^   ^
syn match   smxAttribPunct +[:.]+ contained display

" no highlighting for smxEqual (smxEqual has no highlighting group)
syn match   smxEqual +=+ display

" attributes
"
" PROVIDES: @smxAttribHook
"
" EXAMPLE:
" tag foo.attrib = "value" >
"     ^^^^^^^^^^
syn match smxAttrib 
    \ +[-'"<]\@<!\<[a-zA-Z:_][-.0-9a-zA-Z0-9:_]*\>\(['">]\@!\|$\)+
    \ contained
    \ contains=smxAttribPunct,@smxAttribHook
    \ display

" namespace spec
"
" PROVIDES: @smxNamespaceHook
"
" EXAMPLE:
"
" xsl:for-each select = "lola" >
" ^^^
syn match   smxNamespace
    \ +\(\(^\|&&\)\s*\)\@<=[^ /!?<>"':]\+[:]\@=+
    \ contained
    \ contains=@smxNamespaceHook
    \ display

" tag name
"
" PROVIDES: @smxTagHook
"
" EXAMPLE:
"
" tag foo.attribute = "value">
" ^^^
"
syn match   smxTagName
    \ +\(\(^\|&&\)\s*\)\@<=\([^& /!?<>"'=]\+\)\@>\>\?[^=]\(.\{-\}[>'"]\)\@=+
"\ +\(^\s*\|&&\s*\)\@<=[^& /!?<>"']\+\(\>\?=\)\@!+
    \ contained
    \ contains=smxNamespace,smxAttribPunct,@smxTagHook
    \ display

" comments
syn match   smxComment +^\s*#.*$+

" matches whole tag
"
syn region   smxTag
    \ start=+^+
    \ skip=+['"][^>]*>.\{-\}['"]+
    \ end=+>+
    \ oneline
    \ contains=smxError,smxTagName,smxAttrib,smxEqual,smxString,multiTag,starTag,PITag,@smxStartTagHook

syn match   smxTag
    \ +^\s*[^'"]\+['"]\_.\{-\}['"]\s*?>+
    \ contains=smxError,smxTagName,smxAttrib,smxEqual,smxString,multiTag,starTag,PITag,@smxStartTagHook



hi def link multiTag        WarningMsg
hi def link starTag         Special
hi def link PITag           LineNr
hi def link smxString       String
hi def link Quote           WarningMsg
hi def link strEsc          Special

hi def link smxTodo		Todo
hi def link smxTag		Function
hi def link smxTagName		Function
hi def link smxEndTag		Identifier
if !exists("g:smx_namespace_transparent")
    hi def link smxNamespace	Tag
endif
hi def link smxEntity		Statement
hi def link smxEntityPunct	Type

hi def link smxAttribPunct	Comment
hi def link smxAttrib		Type

hi def link smxString		String
hi def link smxComment		Comment
hi def link smxCommentPart	Comment
hi def link smxCommentError	Error
hi def link smxError		Error

hi def link smxProcessingDelim	Comment
hi def link smxProcessing	Type

hi def link smxCdata		String
hi def link smxCdataCdata	Statement
hi def link smxCdataStart	Type
hi def link smxCdataEnd		Type

hi def link smxDocTypeDecl	Function
hi def link smxDocTypeKeyword	Statement
hi def link smxInlineDTD	Function

let b:current_syntax = "smx"

let &cpo = s:smx_cpo_save
unlet s:smx_cpo_save

