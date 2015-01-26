using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace preprocess
{
    class Program
    {
        static void Main(string[] args)
        {
            if(args.Length > 0) {
                Parser parser = new Parser();
                parser.parse(args[0]);
            }
            else {
                Console.WriteLine("Error!");
            }
            Console.ReadLine();
        }
    }
}
