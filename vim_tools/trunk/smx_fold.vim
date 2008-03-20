" Vim folding file
" Language:	SMX
" Filenames: *.smx
" Author:	Jorrit Wiersma (foldexpr), Max Ischenko (foldtext), Robert
" Ames (line counts), Robert Iannucci (Conversion to SMX)
" Last Change:	2008 Feb 19 
" Version:	s.0
" Bug fix:	Drexler Christopher, Tom Schumm, Geoff Gerrietts


setlocal foldmethod=expr
setlocal foldexpr=GetSMXFold(v:lnum)
"setlocal foldtext=SMXFoldText()


function! SMX()
  let line = getline(v:foldstart)
  let nnum = nextnonblank(v:foldstart + 1)
  let nextline = getline(nnum)
  if nextline =~ '^\s\+"""$'
    let line = line . getline(nnum + 1)
  elseif nextline =~ '^\s\+"""'
    let line = line . ' ' . matchstr(nextline, '"""\zs.\{-}\ze\("""\)\?$')
  elseif nextline =~ '^\s\+"[^"]\+"$'
    let line = line . ' ' . matchstr(nextline, '"\zs.*\ze"')
  elseif nextline =~ '^\s\+pass\s*$'
    let line = line . ' pass'
  endif
  let size = 1 + v:foldend - v:foldstart
  if size < 10
    let size = " " . size
  endif
  if size < 100
    let size = " " . size
  endif
  if size < 1000
    let size = " " . size
  endif
  return size . " lines: " . line
endfunction


function! GetSMXFold(lnum)
    let line = getline(a:lnum)
    let pind = indent(prevnonblank(a:lnum - 1))
    let tind = indent(a:lnum)
    let nind = indent(nextnonblank(a:lnum + 1))
    let lvl = tind / &sw 

    if line =~ '^\s*$' || line !~ '>\s*$'
       return "="
    endif


    if line =~ '>\s*$'
       if nind > tind 
          return '>'.(lvl + 1)
       elseif pind > tind || nind < tind
          return lvl
       endif
    elseif nind == -1
       return lvl + 1
    endif

    return '='
endfunction

