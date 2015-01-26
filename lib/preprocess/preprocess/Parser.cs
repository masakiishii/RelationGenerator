using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;

namespace preprocess
{
    public class Parser
    {
        private int[] maxsize = new int[28];
        public void parse(string filename)
        {
            string line = string.Empty;
            string result = string.Empty;
            string[,] table = new string[50, 28];
            if (System.IO.File.Exists(filename))
            {
                System.IO.StreamReader reader = (new System.IO.StreamReader(filename, System.Text.Encoding.Default));
                int index = 0;
                while (reader.Peek() >= 0)
                {
                    line = reader.ReadLine();
                    string [] splitedline = line.Split('\t');
                    for (int i = 0; i < splitedline.Length; i++ )
                    {
                        table[index, i] = splitedline[i];
                    }
                    index++;
                }
                reader.Close();
            }
            for (int j = 0; j < 28; j++ )
            {
                for (int i = 0; i < 50; i++ )
                {
                    if(table[i, j] != null) {
                        int datalength = table[i, j].Length;
                        this.maxsize[j] = this.maxsize[j] < datalength ? datalength : this.maxsize[j];
                    }
                }
            }
            int spacenum;
            if (!Directory.Exists("test"))
            {
                Directory.CreateDirectory("test");
            }

            string fileName = string.Format(@"test\Rel_time.txt");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 20; i++ )
            {
                for (int j = 0; j < 28; j++ )
                {
                    if(table[i, j] != null) {
                        spacenum = this.maxsize[j] - table[i, j].Length;
                        sb.Append(table[i, j]);
                        for (int k = 0; k < spacenum; k++ )
                        {
                            sb.Append(" ");
                        }
                        sb.Append("& ");
                    }
                }
                sb.Append("\n");
            }
            File.WriteAllText(fileName, sb.ToString());
        }
    }
}
