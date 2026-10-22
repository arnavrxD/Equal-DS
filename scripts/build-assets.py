#!/usr/bin/env python3
"""Turns source art into shipped assets for both platforms.

Lottie: drop `<name>.json` into assets/lottie/; it is copied to res/raw/lottie_<name>.json.

Drop a PNG (transparent, >= 2000px long side, never cropped) into equal-ds/assets/source/ named
like the asset, e.g. `illustration-addresses-empty.png`, then run `npm run assets`. For each file:

  web:     assets/<name>.webp                                   (resized to 4x its display size)
  Compose: compose/.../res/drawable-xxxhdpi/ds_asset_<name_underscored>.webp   (same pixels)

and any checkerboard placeholder `res/drawable/ds_asset_<name>.xml` is deleted. Display sizes
(the long side, in dp) live in SIZES below; unknown names default to 200dp. Category art keeps
its `ds_category_` prefix. Needs Pillow (`pip install pillow`).
"""
import os, sys
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, 'assets', 'source')
RES = os.path.join(ROOT, 'compose', 'equal-ds', 'src', 'main', 'res')

# Longest display side in dp. Shipped at 4x (xxxhdpi).
SIZES = {
    'mascot-robot': 100, 'mascot-sleeping': 104, 'avatar-assistant': 140,
    'avatar-male': 75, 'avatar-female': 75, 'avatar-male-bust': 80, 'avatar-female-bust': 80,
    'illustration-high-five': 113, 'illustration-trash': 72, 'whats-new-illustration': 125,
    'illustration-silent-mode': 160, 'illustration-whatsapp': 160,
    'illustration-addresses-empty': 200, 'illustration-instructions-empty': 200, 'illustration-suggestions': 140,
    'preset-screen-protect': 34, 'preset-everyday-calls': 34, 'preset-business': 34, 'mascot-instructions': 44,
    'permission-microphone': 84, 'avatar-assistant-bust': 200,
    # monetization
    'crown-gold': 100, 'crown-lock': 24, 'coupon-seal': 32, 'star-gold': 24,
    'status-waiting': 100, 'status-waiting-cash': 100, 'status-confirmed': 100, 'status-pending': 100, 'status-failed': 100,
    'emoji-sad': 100, 'thumbs-up': 100,
    'update-art-light': 176, 'update-art-dark': 176,
    'illustration-shield-lock': 140, 'stat-star': 40, 'stat-phone': 40,
    'illustration-airplane': 116, 'illustration-warning': 88, 'illustration-error-sad': 88, 'mascot-update': 160,
    'premium-glow': 360,
    # call handling (Known Calls Flow)
    'call-handling-all': 280, 'call-handling-exclude': 280, 'call-handling-done': 300, 'call-setup': 240,
    'illustration-search': 200, 'illustration-no-internet': 140, 'illustration-no-results': 160,
    'system-keyboard': 390, 'assistant-phone': 40,
    'permission-contacts': 100, 'permission-call-history': 100, 'illustration-no-contacts': 140,
    'feature-call-screening': 32, 'feature-recordings': 32, 'feature-summaries': 32,
    'avatar-testimonial-1': 40, 'avatar-testimonial-2': 40, 'avatar-testimonial-3': 40,
}

def build(path):
    name = os.path.splitext(os.path.basename(path))[0]
    im = Image.open(path).convert('RGBA')
    px = SIZES.get(name, 200) * 4
    w, h = im.size
    scale = min(1.0, px / max(w, h))
    out = im.resize((round(w * scale), round(h * scale)), Image.LANCZOS) if scale < 1 else im
    web = os.path.join(ROOT, 'assets', f'{name}.webp')
    res_name = ('ds_' if name.startswith('category-') else 'ds_asset_') + name.replace('-', '_')
    droid = os.path.join(RES, 'drawable-xxxhdpi', f'{res_name}.webp')
    for p in (web, droid):
        out.save(p, 'WEBP', quality=88, method=6)
    placeholder = os.path.join(RES, 'drawable', f'{res_name}.xml')
    if os.path.exists(placeholder):
        os.remove(placeholder)
    print(f'  {name}: {out.size[0]}x{out.size[1]} -> assets/{name}.webp + drawable-xxxhdpi/{res_name}.webp')

files = [os.path.join(SRC, f) for f in sorted(os.listdir(SRC)) if f.lower().endswith(('.png', '.webp', '.jpg', '.jpeg'))] if os.path.isdir(SRC) else []
if not files:
    print('  no source art in assets/source/ (drop PNGs there, named like the asset)')
for f in files:
    build(f)

# Lottie: assets/lottie/<name>.json -> res/raw/lottie_<name_underscored>.json (web reads assets/lottie directly).
LOTTIE = os.path.join(ROOT, 'assets', 'lottie')
RAW = os.path.join(RES, 'raw')
if os.path.isdir(LOTTIE):
    os.makedirs(RAW, exist_ok=True)
    for f in sorted(os.listdir(LOTTIE)):
        if f.endswith('.json'):
            dest = os.path.join(RAW, 'lottie_' + f[:-5].replace('-', '_') + '.json')
            with open(os.path.join(LOTTIE, f), 'rb') as a, open(dest, 'wb') as b:
                b.write(a.read())
            print(f'  lottie {f} -> res/raw/{os.path.basename(dest)}')
