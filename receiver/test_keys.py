import time
import sys
from receiver import KeyboardInjector

# We need to get the correct injector
if sys.platform == 'linux':
    from receiver import LinuxKeyboardInjector
    InjectorClass = LinuxKeyboardInjector
elif sys.platform == 'win32':
    from receiver import WindowsKeyboardInjector
    InjectorClass = WindowsKeyboardInjector
else:
    print("Unsupported platform for testing")
    sys.exit(1)

def main():
    print("Eden Controller Test Script")
    print("This will sequentially tap all mapped keys.")
    print("Make sure you are focused on the emulator config dialog!")
    print("Starting in 5 seconds... Press Ctrl+C to abort.")
    
    try:
        time.sleep(5)
    except KeyboardInterrupt:
        print("Aborted.")
        sys.exit(0)
        
    injector = InjectorClass()
    
    keys_to_test = [
        'A', 'B', 'X', 'Y', 
        'L', 'R', 'ZL', 'ZR', 
        'MINUS', 'PLUS', 'L3', 'R3', 'WALK',
        'D_UP', 'D_DOWN', 'D_LEFT', 'D_RIGHT',
        'L_UP', 'L_DOWN', 'L_LEFT', 'L_RIGHT',
        'R_UP', 'R_DOWN', 'R_LEFT', 'R_RIGHT'
    ]
    
    print("Starting key sequence...")
    try:
        for key in keys_to_test:
            print(f"Tapping '{key}'...")
            injector.emit_key(key, True)
            injector.sync()
            time.sleep(0.1)
            injector.emit_key(key, False)
            injector.sync()
            time.sleep(0.9)
    except KeyboardInterrupt:
        print("\nInterrupted.")
    finally:
        injector.release_all()
        injector.sync()
        injector.close()
        
    print("Test complete.")

if __name__ == '__main__':
    main()
