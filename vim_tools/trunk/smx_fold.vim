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
    " Determine folding level in Python source
    "
    let line = getline(a:lnum)
    let ind  = indent(a:lnum)

    " Ignore blank lines
    if line =~ '^\s*$'
	return "="
    endif

    " Ignore triple quoted strings
    if line =~ "(\"\"\"|''')"
	return "="
    endif

    " Classes and functions get their own folds
    if line =~ '>\s*$'
	return ">" . (ind / &sw + 1)
    endif

    let pnum = prevnonblank(a:lnum - 1)

    if pnum == 0
	" Hit start of file
	return 0
    endif

    " If the previous line has foldlevel zero, and we haven't increased
    " it, we should have foldlevel zero also
    if foldlevel(pnum) == 0
	return 0
    endif

    " The end of a fold is determined through a difference in indentation
    " between this line and the next.
    " So first look for next line
    let nnum = nextnonblank(a:lnum + 1)
    if nnum == 0
	return "="
    endif

    " Python programmers love their readable code, so they're usually
    " going to have blank lines at the ends of functions or classes
    " If the next line isn't blank, we probably don't need to end a fold
    if nnum == a:lnum + 1
	return "="
    endif

    " If next line has less indentation we end a fold.
    " This ends folds that aren't there a lot of the time, and this sometimes
    " confuses vim.  Luckily only rarely.
    let nind = indent(nnum)
    if nind < ind
	return "<" . (nind / &sw + 1)
    endif

    " If none of the above apply, keep the indentation
    return "="

endfunction


