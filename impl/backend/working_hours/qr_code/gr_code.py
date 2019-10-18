import qrcode
import pyotp
import base64

def generate_qr_code(secret):
    """Generuje obrazek z kodem QR

    Args:
        arg1 (int): Description of arg1
        arg2 (str): Description of arg2

    Returns:
        bool: Description of return value

    """
    secret = base64.b32encode(secret)
    totp = pyotp.TOTP(secret)
    qr = qrcode.QRCode(
    version=1,
    error_correction=qrcode.constants.ERROR_CORRECT_L,
    box_size=10,
    border=4,
    )
    code = totp.now()
    qr.add_data(code)
    qr.make(fit=True)
    print('Code = "' + code + '"')
    return qr.make_image(fill_color="black", back_color="white")

def validate_code(secret, code):
    """Sprawdza poprawność kodu

    Args:
        code (str): Otrzymany od użytkownika kod

    Returns:
        bool: True jesli poprawny False jeśli nie
    """
    secret = base64.b32encode(secret)
    totp = pyotp.TOTP(secret)
    return totp.verify(code)
    