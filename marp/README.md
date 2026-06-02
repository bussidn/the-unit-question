# Slides

Workshop presentation slides using [Marp](https://marp.app/).

## Setup

```bash
brew install marp-cli
```

## Generate HTML

```bash
marp slides.fr.md -o slide.fr.html
```

Then open `slide.fr.html` in a browser.

## Generate the facilitator memo PDF (`print-me.md`)

Uses [`md-to-pdf`](https://github.com/simonhaenisch/md-to-pdf) (Node).

```bash
npm i -g md-to-pdf
md-to-pdf marp/print-me.md --pdf-options '{"format":"A4","margin":"15mm"}'
```

Outputs `marp/print-me.pdf`. First run downloads Chromium (~170 MB).
