import socket
import struct
import argparse
import time
import sys
import signal

# Bitmask Constants
BTN_A = 1 << 0
BTN_B = 1 << 1
BTN_X = 1 << 2
BTN_Y = 1 << 3
BTN_L = 1 << 4
BTN_R = 1 << 5
BTN_ZL = 1 << 6
BTN_ZR = 1 << 7
BTN_MINUS = 1 << 8
BTN_PLUS = 1 << 9
BTN_L3 = 1 << 10
BTN_R3 = 1 << 11
BTN_DPAD_UP = 1 << 12
BTN_DPAD_DOWN = 1 << 13
BTN_DPAD_LEFT = 1 << 14
BTN_DPAD_RIGHT = 1 << 15
BTN_WALK = 1 << 16

class Injector:
    def update(self, buttons, lx, ly, rx, ry, verbose):
        pass
    def release_all(self):
        pass
    def close(self):
        pass

if sys.platform == 'linux':
    try:
        from evdev import UInput, ecodes as e, AbsInfo
    except ImportError:
        print("Please install evdev: pip install evdev")
        sys.exit(1)
        
    class LinuxGamepadInjector(Injector):
        def __init__(self):
            cap = {
                e.EV_KEY: [
                    e.BTN_A, e.BTN_B, e.BTN_X, e.BTN_Y,
                    e.BTN_TL, e.BTN_TR, e.BTN_TL2, e.BTN_TR2,
                    e.BTN_SELECT, e.BTN_START, e.BTN_MODE,
                    e.BTN_THUMBL, e.BTN_THUMBR,
                    e.BTN_DPAD_UP, e.BTN_DPAD_DOWN, e.BTN_DPAD_LEFT, e.BTN_DPAD_RIGHT,
                ],
                e.EV_ABS: [
                    (e.ABS_X, AbsInfo(value=0, min=-127, max=127, fuzz=0, flat=0, resolution=0)),
                    (e.ABS_Y, AbsInfo(value=0, min=-127, max=127, fuzz=0, flat=0, resolution=0)),
                    (e.ABS_RX, AbsInfo(value=0, min=-127, max=127, fuzz=0, flat=0, resolution=0)),
                    (e.ABS_RY, AbsInfo(value=0, min=-127, max=127, fuzz=0, flat=0, resolution=0)),
                ]
            }
            self.ui = UInput(cap, name="Eden Virtual Gamepad")
            self.prev_buttons = 0
            self.prev_lx = 0
            self.prev_ly = 0
            self.prev_rx = 0
            self.prev_ry = 0
            
            # Map Android bitmask directly to Linux gamepad standard buttons
            # The emulator (Yuzu/Ryujinx) will recognize this as a generic gamepad.
            # Nintendo layout -> Standard evdev Gamepad mapping:
            # A is East, B is South, X is North, Y is West.
            self.btn_map = {
                BTN_A: e.BTN_EAST,
                BTN_B: e.BTN_SOUTH,
                BTN_X: e.BTN_NORTH,
                BTN_Y: e.BTN_WEST,
                BTN_L: e.BTN_TL,
                BTN_R: e.BTN_TR,
                BTN_ZL: e.BTN_TL2,
                BTN_ZR: e.BTN_TR2,
                BTN_MINUS: e.BTN_SELECT,
                BTN_PLUS: e.BTN_START,
                BTN_L3: e.BTN_THUMBL,
                BTN_R3: e.BTN_THUMBR,
                BTN_DPAD_UP: e.BTN_DPAD_UP,
                BTN_DPAD_DOWN: e.BTN_DPAD_DOWN,
                BTN_DPAD_LEFT: e.BTN_DPAD_LEFT,
                BTN_DPAD_RIGHT: e.BTN_DPAD_RIGHT,
                BTN_WALK: e.BTN_MODE,
            }
            
        def update(self, buttons, lx, ly, rx, ry, verbose):
            changed_buttons = self.prev_buttons ^ buttons
            if changed_buttons:
                for mask, ecode in self.btn_map.items():
                    if changed_buttons & mask:
                        state = 1 if (buttons & mask) else 0
                        self.ui.write(e.EV_KEY, ecode, state)
            
            if lx != self.prev_lx: self.ui.write(e.EV_ABS, e.ABS_X, lx)
            if ly != self.prev_ly: self.ui.write(e.EV_ABS, e.ABS_Y, ly)
            if rx != self.prev_rx: self.ui.write(e.EV_ABS, e.ABS_RX, rx)
            if ry != self.prev_ry: self.ui.write(e.EV_ABS, e.ABS_RY, ry)
            
            self.ui.syn()
            
            if verbose and (changed_buttons or lx != self.prev_lx or ly != self.prev_ly or rx != self.prev_rx or ry != self.prev_ry):
                print(f"State -> Buttons: {bin(buttons)} L:({lx},{ly}) R:({rx},{ry})")
            
            self.prev_buttons = buttons
            self.prev_lx = lx
            self.prev_ly = ly
            self.prev_rx = rx
            self.prev_ry = ry
            
        def release_all(self):
            self.update(0, 0, 0, 0, 0, False)
            
        def close(self):
            self.ui.close()
            
elif sys.platform == 'win32':
    try:
        import keyboard
    except ImportError:
        print("Please install keyboard: pip install keyboard")
        sys.exit(1)
        
    class WindowsKeyboardInjector(Injector):
        def __init__(self):
            self.prev_buttons = 0
            self.key_map = {
                BTN_A: 'c', BTN_B: 'x', BTN_X: 'v', BTN_Y: 'z',
                BTN_L: 'q', BTN_R: 'e', BTN_ZL: 'r', BTN_ZR: 't',
                BTN_MINUS: 'n', BTN_PLUS: 'm',
                BTN_DPAD_UP: 'up', BTN_DPAD_DOWN: 'down', 
                BTN_DPAD_LEFT: 'left', BTN_DPAD_RIGHT: 'right',
            }
        def update(self, buttons, lx, ly, rx, ry, verbose):
            changed = self.prev_buttons ^ buttons
            if changed:
                for mask, k in self.key_map.items():
                    if changed & mask:
                        if buttons & mask: keyboard.press(k)
                        else: keyboard.release(k)
            self.prev_buttons = buttons
            
        def release_all(self):
            self.update(0, 0, 0, 0, 0, False)

else:
    class DummyInjector(Injector):
        pass
    print("Warning: Unsupported platform. Running in dummy mode.")


def process_packet(data, injector, previous_state, verbose):
    if len(data) != 12:
        return previous_state
        
    magic, version, seq, buttons, lx, ly, rx, ry = struct.unpack('<BBHIbbbb', data)
    
    if magic != 0xC7 or version != 1:
        return previous_state

    # Sequence number wraparound handling
    diff = (seq - previous_state.get('seq', seq - 1)) & 0xFFFF
    if diff == 0 or diff > 0x7FFF: 
        if 'seq' in previous_state:
            return previous_state

    injector.update(buttons, lx, ly, rx, ry, verbose)
    return {'seq': seq}


def main():
    parser = argparse.ArgumentParser(description='Eden Controller Receiver')
    parser.add_argument('--bind', default='0.0.0.0', help='IP to bind to')
    parser.add_argument('--port', type=int, default=9876, help='UDP port')
    parser.add_argument('--verbose', action='store_true', help='Print state changes')
    args = parser.parse_args()
    
    if sys.platform == 'linux':
        injector = LinuxGamepadInjector()
    elif sys.platform == 'win32':
        injector = WindowsKeyboardInjector()
    else:
        injector = DummyInjector()
        
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((args.bind, args.port))
    sock.settimeout(0.5)
    
    print(f"Listening on {args.bind}:{args.port} (UDP)")
    
    running = True
    def handle_sigint(sig, frame):
        nonlocal running
        print("\nShutting down...")
        running = False
        
    signal.signal(signal.SIGINT, handle_sigint)
    signal.signal(signal.SIGTERM, handle_sigint)

    state = {}
    
    try:
        while running:
            try:
                data, addr = sock.recvfrom(1024)
                state = process_packet(data, injector, state, args.verbose)
            except socket.timeout:
                injector.release_all()
            except BlockingIOError:
                pass
            except Exception as e:
                print(f"Error receiving packet: {e}")
                injector.release_all()
    finally:
        injector.release_all()
        injector.close()
        sock.close()

if __name__ == '__main__':
    main()
