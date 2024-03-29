package ru.pcom.app.rs;

import java.util.ListResourceBundle;

public class Text extends ListResourceBundle {
    public Object[][] getContents() {
        return new Object[][] {
            { "app", "PCom"},
            { "lang", "EN"},
            { "app_ver", "The PCom 1.0 alpha"},
            { "app_build", "100"},
            { "copyright", "\u00a9 2019-2023 Igor V. Polyakoff, Russia"},
            { "app_descr", "A cross-platform orthodox file manager"},
            { "copyright_warn", "Warning: This computer program is protected by copyright law and international treaties. Unauthorized reproduction or distribution of this program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible under the law."},
            { "web", "Web"},
            { "email", "E-mail"},
            { "error", "Error"},
            { "yes", "Yes"},
            { "no", "No"},
            { "cancel", "Cancel"},

            { "left", "_Left" }, // main menu
            { "file", "_Files" }, // main menu
            { "edit", "_Edit" },
            { "commands", "_Commands" },
            { "options", "_Options" },
            { "right", "_Right" },
            { "help", "_Help" },
            { "exit", "E_xit" },
            { "about", "_About..." },
            { "about2", "About PCom" },

            { "goto", "_Go To" },
            { "back", "_Back" },
            { "forward", "_Forward" },
            { "chdrive", "Change _Drive" },
            { "refresh", "Refre_sh" },

            { "help2", "Help" }, // F-bar
            { "rename2", "Re_name" },
            { "view", "View" },
            { "edit2", "Edit" },
            { "copy", "Copy" },
            { "move", "Move" },
            { "copy2", "Co_py" },
            { "move2", "Mo_ve/Rename" },
            { "mkdir", "_New folder" },
            { "delete", "Delete" },
            { "delete2", "_Delete" },
            { "usermenu", "User menu" },
            { "menu", "Menu" },
            { "pan100s", "Maximize" },
            { "menu_tt", "Main menu" },

            { "pan_col_name", "Name" }, // panel
            { "pan_col_filename", "File name" },
            { "pan_col_ext", "Ext." },
            { "pan_col_active", "Active" },
            { "pan_col_size", "Size" },
            { "pan_col_date", "Date" },
            { "pan_col_time", "Time" },
            { "pan_col_attr", "Attributes" },
            { "clip_copy", "Copy to clipboard" },

            { "pan_no_files", "No files." },

            { "button_disk", "Disk" },
            { "addpanel", "Add panel" },
            { "root_folder", "Root folder" },
            { "home_folder", "Home folder" },

            { "err_cfg_read", "Error reading settings" },
            { "err_cfg_wrt", "Error writing settings" },
            { "err_incorr_data", "Incorrect data." },
            { "err_no_data", "No data." },
            { "err_open_item", "Error opening item" },
            { "err_open_file", "Error opening file" },
            { "err_open_folder", "Error opening folder" },
            { "err_list_folder", "Cannot read the folder" },
            { "err_open_symlink", "error resolving symbolic link" },
            { "err_desktop_notsupp", "Desktop is not supported" },
            { "err_home_folder", "Error opening home folder" },
            { "err_home_folder_no", "Path not determined" },
            { "err_home_folder_not_ex", "Folder not found" },
            { "err_file_not_ex", "file does not exist" },
            { "err_folder_not_ex", "folder does not exist" },
            { "err_unknown_fof", "unknown type of file system object" },
            { "err_delete_file", "Error deleting file" },
            { "err_get_resource", "no resource" },
            { "err_mk_dir", "Error creating folder" },
            { "err_rename", "Error changing name" },
            { "unable_delete_ren", "Unable to remove the file to be replaced" },

            { "folder_exists", "The folder or file already exists" },
            { "file_exists", "The file already exists" },
            { "Access denied", "Access denied" },
            { "folder_invalid_sym", "The folder name syntax is incorrect" },
            { "name_invalid_sym", "The filename or directory name syntax is incorrect" },
            { "folder_invalid", "The specified name is incorrect" },
            { "not_specified", "Empty name is invalid" },
            { "New Folder", "New Folder" },
            { "Folder name", "Folder name" },

            { "QuickRename", "Quick Rename" },
            { "QuickRenamePrompt", "Rename \"%s\" to" },

            { "fcopy", "Copy" },
            { "fmove", "Move/Rename" },
            { "fcopy2", "Copy %s to" },
            { "fmove2", "Move %s to" },
            { "fdelete", "Delete" },
            { "delete_prompt", "Delete %s?" },
            { "Confirm Delete", "Confirm Delete" },
            { "err_copy_file", "Copy Error" },
            { "err_move_file", "Move Error" },
            { "err_del", "Delete Error" },
            { "err_invalid_path", "Invalid path specified" },
            { "Question", "Question" },
            { "Cancel_oper", "Do you want to cancel this operation?" },

            { "file_replace_confirm", "Confirm File Overwrite" },
            { "replace_file_1", "Overwrite file" },
            { "replace_file_2", "With file" },
            { "to", "to" },
            { "Done", "Done" },

            { "folder", "Folder" },
            { "val_byte", "b" },
            { "val_kb", "Kb" },
            { "val_mb", "Mb" },
            { "val_gb", "Gb" },
            { "val_tb", "Tb" },
            { "val_pb", "Pb" },
            { "auto", "Auto" },

            { "folder1", "folder" },
            { "folders", "folders" },
            { "folders2", "folders" },
            { "folder1a", "folder" },
            { "file1", "file" },
            { "files", "files" },
            { "files2", "files" },
            { "status_select", "%s selected" },

            { "left100", "Maximize _left panel" },
            { "right100", "Maximize _right panel" },
            { "pan5050", "Restore panel size (_50/50)" },
            { "pan100", "_Maximize panel" },

            { "Appearance", "Appearance" },
            { "Font", "Font" },
            { "quick_fox", "The quick brown fox jumps over the lazy dog." },
            { "font_reload_app", "The font settings will be applied after program restarts." },
            { "lang_reload_app", "The language settings will be applied after program restarts." },

            { "General", "General" },
            { "eager_load", "_Read all panels contents at startup" },
            { "hide_files", "_Hide files and folders with hidden or system attribute (Ctrl+H)" },
            { "read_on_activ", "Re_fresh panels on activation" },
            { "sz_unit", "Display file size" },

            { "Language", "Language" },

            {"ask_quit", "Do you want to quit the PCom?"}

        };
    }
}
