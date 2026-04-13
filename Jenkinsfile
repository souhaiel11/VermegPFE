def get_file_preview(file):
    # New line: import os
    if not os.access(file, os.R_OK):
        return None
    # Original code:
    with open(file, 'rb') as fp:
        preview = fp.read(1024)
        if not preview:
            return None
        return preview